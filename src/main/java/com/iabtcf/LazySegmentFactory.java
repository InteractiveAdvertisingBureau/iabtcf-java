package com.iabtcf;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Given a ASCII String of the form XXXX.YYYYY.ZZZZ..., this class returns each segment as an
 * InputStream by successive invocations of {@link LazySegmentFactory#next()}. The starting
 * offsets of each segment are computed in a lazy manner. That is, the start offsets are
 * resolved recursively by the nth segment querying the nth-1 segment for it's ending location.
 */
public class LazySegmentFactory {
    private SegmentSupplier sup;
    private final String src;

    public LazySegmentFactory(String src) {
        this.src = src;
        sup = new SegmentSupplier();
    }

    public Supplier<InputStream> next() {
        SegmentSupplier prev = sup;
        sup = new SegmentSupplier(prev);
        return prev;
    }

    private static class EmptyInputStream extends SegmentInputStream {
        public static EmptyInputStream INSTANCE = new EmptyInputStream();

        private EmptyInputStream() {
            super("", 0);
        }

        @Override
        protected boolean hasNextSegment() {
            return false;
        }

        @Override
        protected int segmentEnd() {
            return -1;
        }
    }

    class SegmentSupplier implements Supplier<InputStream> {
        private final SegmentSupplier prev;
        private SegmentInputStream current;

        public SegmentSupplier() {
            this.prev = null;
            this.current = new SegmentInputStream(src, 0);
        }

        public SegmentSupplier(SegmentSupplier prev) {
            assert (prev != null);

            this.prev = prev;
        }

        private SegmentInputStream getCurrent() {
            if (current == null) {
                SegmentInputStream prevStream = prev.getCurrent();
                if (prevStream == EmptyInputStream.INSTANCE) {
                    current = EmptyInputStream.INSTANCE;
                } else {
                    current = new SegmentInputStream(LazySegmentFactory.this.src, prevStream.segmentEnd() + 1);
                }
            }

            return current;
        }

        @Override
        public InputStream get() {
            return getCurrent();
        }
    }
}