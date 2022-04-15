package aQute.json.codec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import aQute.lib.converter.Converter;

public class NumberHandler extends Handler {
	
	interface Encode {
		void encode(Encoder e, Object o) throws Exception;
	}
	
	@SuppressWarnings("rawtypes")
	final static Map<Class, Encode> appenders = new HashMap<>();
	static {
		appenders.put(Float.class, NumberHandler::appendFloat);
		appenders.put(Double.class, NumberHandler::appendDouble);
		appenders.put(float.class, NumberHandler::appendFloat);
		appenders.put(double.class, NumberHandler::appendDouble);
	}

	final Class<?>									type;
	final boolean									doubles;
	final boolean									floats;

	final Encode	encoder;

	NumberHandler(Class<?> clazz) {
		this.type = clazz;
		this.doubles = clazz == Double.class;
		this.floats = clazz == Float.class;
		this.encoder = appenders.getOrDefault(clazz, NumberHandler::append);
	}

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws Exception {
		encoder.encode(app, object);
	}

	@Override
	public Object decode(Decoder dec, boolean s) {
		return decode(dec, s ? 1d : 0d);
	}

	@Override
	public Object decode(Decoder dec) {
		return decode(dec, 0d);
	}

	@Override
	public Object decode(Decoder dec, String s) throws Exception {

		if (doubles) {
			if (s.equalsIgnoreCase("NaN"))
				return Double.NaN;
			if (s.equalsIgnoreCase("+Infinity") || s.equalsIgnoreCase("Infinity"))
				return Double.POSITIVE_INFINITY;
			if (s.equalsIgnoreCase("-Infinity"))
				return Double.NEGATIVE_INFINITY;
			return Double.parseDouble(s);
		} else if (floats) {
			if (s.equalsIgnoreCase("NaN"))
				return Float.NaN;
			if (s.equalsIgnoreCase("+Infinity") || s.equalsIgnoreCase("Infinity"))
				return Float.POSITIVE_INFINITY;
			if (s.equalsIgnoreCase("-Infinity"))
				return Float.NEGATIVE_INFINITY;
			return Float.parseFloat(s);
		}
		return Converter.cnv(type, s);
	}

	@Override
	public Object decode(Decoder dec, Number s) {
		double dd = s.doubleValue();

		if (type == double.class || type == Double.class)
			return s.doubleValue();

		if ((type == int.class || type == Integer.class) && within(dd, Integer.MIN_VALUE, Integer.MAX_VALUE))
			return s.intValue();

		if ((type == long.class || type == Long.class) && within(dd, Long.MIN_VALUE, Long.MAX_VALUE))
			return s.longValue();

		if ((type == byte.class || type == Byte.class) && within(dd, Byte.MIN_VALUE, Byte.MAX_VALUE))
			return s.byteValue();

		if ((type == short.class || type == Short.class) && within(dd, Short.MIN_VALUE, Short.MAX_VALUE))
			return s.shortValue();

		if (type == float.class || type == Float.class)
			return s.floatValue();

		if (type == BigDecimal.class)
			return BigDecimal.valueOf(dd);

		if (type == BigInteger.class)
			return BigInteger.valueOf(s.longValue());

		throw new IllegalArgumentException("Unknown number format: " + type);
	}

	private boolean within(double s, double minValue, double maxValue) {
		return s >= minValue && s <= maxValue;
	}

	public static void append(Encoder enc, long s) throws IOException {
		enc.append(Long.toString(s));
	}
	
	


	public static void append(Encoder enc, int v) throws IOException {
		enc.append(Integer.toString(v));
//        if (v < 0) {
//            if (v == Integer.MIN_VALUE) {
//                //
//                // would come out as -0
//                //
//                enc.append("-2147483648");
//                return;
//            }
//
//            enc.append('-');
//            v = -v;
//        }
//        while (true) {
//
//            if (v < 10) {
//                enc.appendChar(v + '0');
//                return;
//            }
//            if (v < 100) {
//                enc.appendChar((v / 10) + '0');
//                enc.appendChar((v % 10) + '0');
//                return;
//            }
//            if (v < 1000) {
//                enc.appendChar((v / 100) + '0');
//                v %= 100;
//                enc.appendChar((v / 10) + '0');
//                enc.appendChar((v % 10) + '0');
//                return;
//            }
//            if (v < 10_000) {
//                enc.appendChar((v / 1000) + '0');
//                v %= 1000;
//                enc.appendChar((v / 100) + '0');
//                v %= 100;
//                enc.appendChar((v / 10) + '0');
//                enc.appendChar((v % 10) + '0');
//                return;
//            }
//            if (v < 100_000) {
//                enc.appendChar((v / 10_000) + '0');
//                v %= 10_000;
//                enc.appendChar((v / 1000) + '0');
//                v %= 1000;
//                enc.appendChar((v / 100) + '0');
//                v %= 100;
//                enc.appendChar((v / 10) + '0');
//                enc.appendChar((v % 10) + '0');
//                return;
//            }
//            if (v < 1_000_000) {
//                enc.appendChar((v / 100_000) + '0');
//                v %= 100_000;
//                enc.appendChar((v / 10_000) + '0');
//                v %= 10_000;
//                enc.appendChar((v / 1000) + '0');
//                v %= 1000;
//                enc.appendChar((v / 100) + '0');
//                v %= 100;
//                enc.appendChar((v / 10) + '0');
//                enc.appendChar((v % 10) + '0');
//                return;
//            }
//            if (v < 10_000_000) {
//                enc.appendChar((v / 1_000_000) + '0');
//                v %= 1_000_000;
//                continue;
//            }
//            if (v < 100_000_000) {
//                enc.appendChar((v / 10_000_000) + '0');
//                v %= 10_000_000;
//                continue;
//            }
//            if (v < 1_000_000_000) {
//                enc.appendChar((v / 100_000_000) + '0');
//                v %= 100_000_000;
//                continue;
//            }
//
//            enc.appendChar((v / 1_000_000_000) + '0');
//            v %= 1_000_000_000;
//        }
    }

	public static void append(Encoder enc, float f) throws IOException {
		if (Float.isNaN(f)) {
			enc.append("\"NaN\"");
		} else if (Float.isInfinite(f)) {
			if (f > 0)
				enc.append("\"Infinity\"");
			else
				enc.append("\"-Infinity\"");
		} else {
			String string = Float.toString(f);
			if (string.endsWith(".0")) {
				int l = string.length();
				for (int i = 0; i < l - 2; i++) {
					enc.append(string.charAt(i));
				}
			} else
				enc.append(string);
		}
	}

	public static void append(Encoder enc, double f) throws IOException {
		if (Double.isNaN(f)) {
			enc.append("\"NaN\"");
		} else if (Double.isInfinite(f)) {
			if (f > 0)
				enc.append("\"Infinity\"");
			else
				enc.append("\"-Infinity\"");
		} else {
			String string = Double.toString(f);
			if (string.endsWith(".0")) {
				int l = string.length();
				for (int i = 0; i < l - 2; i++) {
					enc.append(string.charAt(i));
				}
			} else
				enc.append(string);
		}
	}

	private static void append(Encoder enc, Object v) throws IOException {
		enc.append(v.toString());
	}

	private static void appendFloat(Encoder enc, Object flt) throws IOException {
		float v = (Float) flt;
		append(enc, v);
	}

	private static void appendDouble(Encoder enc, Object dbl) throws IOException {
		double v = (Double) dbl;
		append(enc, v);
	}

}
