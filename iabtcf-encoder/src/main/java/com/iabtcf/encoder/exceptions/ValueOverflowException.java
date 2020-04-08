package com.iabtcf.encoder.exceptions;

/*-
 * #%L
 * IAB TCF Java Encoder Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Optional;

import com.iabtcf.utils.FieldDefs;

public class ValueOverflowException extends RuntimeException {
    private static final long serialVersionUID = 8604885489107552868L;
    private final Optional<FieldDefs> field;
    private final Optional<Long> max;
    private final Optional<Long> value;

    public ValueOverflowException() {
        super();

        this.field = Optional.empty();
        this.max = Optional.empty();
        this.value = Optional.empty();
    }

    @Override
    public String toString() {
        return "ValueOverflowException [field=" + field + ", max=" + max + ", value=" + value + "]";
    }

    public ValueOverflowException(long value, FieldDefs field) {
        this.max = Optional.empty();
        this.value = Optional.of(value);
        this.field = Optional.empty();
    }

    public ValueOverflowException(long value, long max, FieldDefs field) {
        this.max = Optional.of(max);
        this.value = Optional.of(value);
        this.field = Optional.of(field);
    }

    public ValueOverflowException(long value, long max) {
        this.max = Optional.empty();
        this.value = Optional.of(value);
        this.field = Optional.empty();
    }

    public Optional<FieldDefs> getField() {
        return field;
    }
}
