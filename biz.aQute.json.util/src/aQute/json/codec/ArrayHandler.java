package aQute.json.codec;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.lib.hex.Hex;

@SuppressWarnings("rawtypes")
public class ArrayHandler extends Handler {
	static Lookup lookup = MethodHandles.lookup();

	interface EncodeArray {
		void array(Object array, Encoder enc, Map<Object, Type> visited) throws Exception;
	}

	interface DecodeArray {
		Object array(Decoder dec, ArrayHandler h) throws Exception;
	}

	final static Map<Class, EncodeArray>		encoders	= new HashMap<>();
	final static Map<Class, DecodeArray>	decoders	= new HashMap<>();
	static {
		encoders.put(boolean.class, ArrayHandler::booleanarray);
		encoders.put(byte.class, ArrayHandler::bytearray);
		encoders.put(char.class, ArrayHandler::chararray);
		encoders.put(short.class, ArrayHandler::shortarray);
		encoders.put(int.class, ArrayHandler::intarray);
		encoders.put(long.class, ArrayHandler::longarray);
		encoders.put(float.class, ArrayHandler::floatarray);
		encoders.put(double.class, ArrayHandler::doublearray);
		decoders.put(boolean.class, ArrayHandler::decodeBoolean);
		decoders.put(byte.class, ArrayHandler::decodeByte);
		decoders.put(char.class, ArrayHandler::decodeChar);
		decoders.put(short.class, ArrayHandler::decodeShort);
		decoders.put(int.class, ArrayHandler::decodeInt);
		decoders.put(long.class, ArrayHandler::decodeLong);
		decoders.put(float.class, ArrayHandler::decodeFloat);
		decoders.put(double.class, ArrayHandler::decodeDouble);
	}

	final Type			componentType;
	final EncodeArray		encoder;
	final DecodeArray	decoder;
	final Class			componentRawClass;
	final Object		empty;

	ArrayHandler(Class<?> rawClass, Type componentType)
			throws IllegalAccessException, NoSuchMethodException, SecurityException {
		this.componentType = componentType;
		this.componentRawClass = JSONCodec.getRawClass(componentType);
		this.empty = Array.newInstance(this.componentRawClass, 0);
		encoder = encoders.getOrDefault(componentType, this::objectarray);
		decoder = decoders.getOrDefault(componentType, ArrayHandler::decodeObjects);
	}

	@Override
	public void encode(Encoder app, Object object, Map<Object, Type> visited) throws IOException, Exception {
		encoder.array(object, app, visited);
	}

	@Override
	public Object decodeArray(Decoder r) throws Exception {
		return decoder.array(r, this);
	}

	private void objectarray(Object v, Encoder enc, Map<Object, Type> visited) throws Exception {
		enc.append('[');
		enc.indent();
		Object[] values = (Object[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			enc.encode(values[i], componentType, visited);
		}
		enc.undent();
		enc.append(']');
	}

	private static void shortarray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		short[] values = (short[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static void intarray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		int[] values = (int[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static void longarray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		long[] values = (long[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static void booleanarray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		boolean[] values = (boolean[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			if (values[i]) {
				enc.append("true");
			} else {
				enc.append("false");
			}
		}
		enc.undent();
		enc.append(']');
	}

	private static void bytearray(Object v, Encoder app, Map<Object, Type> visited) throws IOException {
		StringHandler.string(app, Hex.toHexString((byte[]) v));
	}

	private static void chararray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		char[] values = (char[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static void floatarray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		float[] values = (float[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static void doublearray(Object v, Encoder enc, Map<Object, Type> visited) throws IOException {
		enc.append('[');
		enc.indent();
		double[] values = (double[]) v;
		int l = values.length;
		for (int i = 0; i < l; i++) {
			if (i != 0)
				enc.append(',');
			NumberHandler.append(enc, values[i]);
		}
		enc.undent();
		enc.append(']');
	}

	private static Object decodeObjects(Decoder r, ArrayHandler handler) {
		try {
			List<Object> list = new ArrayList<>();
			r.codec.parseArray(r, rr -> {
				Object decode = r.decode(handler.componentType);
				list.add(decode);
			});
			return list.toArray((Object[]) handler.empty);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static Object decodeBoolean(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, boolean.class);
		boolean[] array = new boolean[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = BooleanHandler.isTruthy(o);
		}
		return array;
	}

	private static Object decodeByte(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, byte.class);
		byte[] array = new byte[list.size()];
		int n = 0;
		for (Object o : list) {
			long number = toLong(o);
			if (number < Byte.MIN_VALUE || number > Byte.MAX_VALUE) {
				throw new IllegalArgumentException(
						"destination is a byte array but the value is outside the byte range of -128,127: " + number);
			}
			array[n++] = (byte) number;
		}
		return array;
	}

	private static Object decodeChar(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, char.class);
		char[] array = new char[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = (char) o;
		}
		return array;
	}

	private static Object decodeShort(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, short.class);
		short[] array = new short[list.size()];
		int n = 0;
		for (Object o : list) {
			long number = toLong(o);
			if (number < Short.MIN_VALUE || number > Short.MAX_VALUE) {
				throw new IllegalArgumentException(
						"destination is a short array but the value is outside the short range of " + Short.MIN_VALUE
								+ "," + Short.MAX_VALUE + ": " + number);
			}
			array[n++] = (short) number;
		}
		return array;
	}

	private static Object decodeInt(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, int.class);
		int[] array = new int[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = (int) o;
		}
		return array;
	}

	private static Object decodeLong(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, long.class);
		long[] array = new long[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = (long) o;
		}
		return array;
	}

	private static Object decodeFloat(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, float.class);
		float[] array = new float[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = (float) o;
		}
		return array;
	}

	private static Object decodeDouble(Decoder dec, ArrayHandler handler) throws Exception {
		List<Object> list = getList(dec, double.class);
		double[] array = new double[list.size()];
		int n = 0;
		for (Object o : list) {
			array[n++] = (double) o;
		}
		return array;
	}

	private static long toLong(Object o) {
		if (o == null)
			return 0;
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		return 0;
	}

	private static List<Object> getList(Decoder dec, Class c) throws Exception {
		List<Object> list = new ArrayList<>();
		dec.codec.parseArray(dec, rr -> {
			Object decode = rr.decode(c);
			list.add(decode);
		});
		return list;
	}

}
