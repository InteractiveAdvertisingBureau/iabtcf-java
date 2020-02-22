package com.iabtcf.decoder;

import com.iabtcf.PublisherRestriction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ewhite 2/22/20
 */
public class Util {

	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

	static String base64FromBitString(String str) {
		List<Byte> byteList = new ArrayList<>();
		for (int i = 0; i < str.length(); ) {
			String s = "";
			for (int j = 0; j < 8 && i < str.length(); j++) {
				s += str.charAt(i);
				i++;
			}
			byteList.add((byte) Integer.parseInt(s, 2));
		}
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

	// since we don't have access to Sets.newHashSet() from java 9
	public static Set<Integer> setOf(Integer... ints) {
		return new HashSet<>(Arrays.asList(ints));
	}

	public static Set<PublisherRestriction> setOf(PublisherRestriction... ints) {
		return new HashSet<>(Arrays.asList(ints));
	}
}
