package com.zarbosoft.pidgoon.internal;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedBytes;

public class Aux {

	public static String byteFormat(byte b) {
		if (b == (byte)'\n') return "\\n";
		if (b == (byte)'\r') return "\\r";
		if (b == (byte)'\t') return "\\t";
		if ((b < 32) || (b >= 127)) return String.format("\\x%s", Strings.padStart(UnsignedBytes.toString(b), 2, '0'));
		return Character.toString((char)(byte)b);
	}

	public static String byteFormat(List<Byte> bytes) {
		return bytes.stream()
			.map(b -> byteFormat(b))
			.collect(Collectors.joining());
	}

}
