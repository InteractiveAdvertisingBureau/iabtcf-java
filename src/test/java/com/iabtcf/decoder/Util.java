package com.iabtcf.decoder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * @author ewhite 2/22/20
 */
public class Util {

	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

	static String base64FromBitString(String str) {
		List<Byte> byteList = new ArrayList<>();
		for (int i = 0; i < str.length(); ) {
			StringBuilder s = new StringBuilder();
			for (int j = 0; j < 8 && i < str.length(); j++) {
				s.append(str.charAt(i++));
			}
			if (s.length() < 8) {
				// right pad the end with 0's
				s.append(String.join("", Collections.nCopies((8 - s.length()), "0")));
			}
			byteList.add((byte) Integer.parseInt(s.toString(), 2));
		}
		// build last right padded byte
		byte[] bytes = new byte[byteList.size()];
		int i = 0;
		for (Byte aByte : byteList) {
			bytes[i++] = aByte;
		}
		return Base64.getUrlEncoder().encodeToString(bytes);
	}

	static BitVector vectorFromBase64String(final String str) {
		byte[] bytes = DECODER.decode(str);
		return BitVector.from(bytes);
	}

	static BitVector vectorFromBitString(final String str) {
		byte[] bytes = DECODER.decode(base64FromBitString(str));
		return BitVector.from(bytes);
	}

	public static BitSet bitSetOf(Integer... ints) {
		final BitSet bitSet = new BitSet(ints.length);
		for (int i : ints) {
			bitSet.set(i);
		}
		return bitSet;
	}
}
