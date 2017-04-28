package aQute.json.naming;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Formatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * This class encodes and decodes any string to a valid Java Identifier.
 *
 * The rules are simple. If the string is a reserved word in Java then the
 * string is returned with a final '$' to no longer make it a reserved word.
 *
 * Otherwise, each character is inspected. If it is a '$' or not a valid Java
 * Identifier Start/Part then it is encoded with a '$' and a the hex digits of
 * its UTF-8 encoding. The encoding properly handles all code points.
 *
 * Some examples:
 *
 * <pre>
 *		abc			abc
 *		default		default$
 *		abc-def		abc$2Ddef
 *		_🜐_			_$F09F9C90_
 * </pre>
 */
public class NameCodec {

	/**
	 * Encode a name to a hex encoded name that is a valid Java identifier
	 *
	 * @param name
	 *            the unencoded name that can contain any character
	 * @return
	 */
	public static String encode(String name) {
		int index = Arrays.binarySearch(RESERVED_JAVA, name);
		if (index > 0)
			return name + "$";

		try (Formatter encoded = new Formatter();) {

			AtomicBoolean first = new AtomicBoolean(true);

			name.codePoints().forEach(cp -> {
				boolean isValid = (cp != '$');

				isValid &= ((first.get()
						&& Character.isJavaIdentifierStart(cp))
						|| (!first.get()
								&& Character.isJavaIdentifierPart(cp)));

				isValid &= cp > ' ';

				if (isValid) {
					encoded.format("%c", cp);
				} else {
					String s = new String(new int[] { cp }, 0, 1);
					ByteBuffer b = StandardCharsets.UTF_8.encode(s);
					encoded.format("$");
					while (b.remaining() > 0) {
						byte v = b.get();
						encoded.format("%02X", v);
					}
				}
				first.set(false);
			});
			return encoded.toString();
		}
	}

	/**
	 * Decode a name that was encoded with the encode method.
	 *
	 * @param name
	 *            the encoded name that is a valid Java Identifier
	 * @return the decoded name
	 */
	public static String decode(Field field) {
		Name key = field.getAnnotation(Name.class);
		if (key != null)
			return key.value();

		return decode(field.getName());
	}

	/**
	 * Decode a name that was encoded with the encode method.
	 *
	 * @param name
	 *            the encoded name that is a valid Java Identifier
	 * @return the decoded name
	 */
	public static String decode(String name) {
		if ( name.equals("$ref"))
			return name;

		if (name.endsWith("$"))
			return name.substring(0, name.length() - 1);

		StringBuilder decoded = new StringBuilder();
		ByteBuffer buffer = ByteBuffer.allocate(3);

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '$') {
				buffer.clear();
				byte first = getHex(name, i);
				buffer.put(first);
				i += 2;

				int n = toLength(first);
				while (n > 0) {
					n--;
					byte following = getHex(name, i);
					i += 2;
					assert (0b11_00_0000 & following) == 0b10_00_0000;
					buffer.put(following);
				}

				buffer.flip();
				CharBuffer d = StandardCharsets.UTF_8.decode(buffer);
				decoded.append(d.toString());
			} else {
				decoded.append(c);
			}
		}
		return decoded.toString();
	}

	private static int toLength(byte first) {
		if ((first & 0b1111_1_000) == 0b1111_0_000) {
			return 3;
		}
		if ((first & 0b1111_0000) == 0b1110_0000) {
			return 2;
		}
		if ((first & 0b1110_0000) == 0b1100_0000) {
			return 1;
		}
		return 0;
	}

	private static byte getHex(String name, int i) {
		int v;
		v = 0XF0 & (nibble(name.charAt(i + 1)) << 4);
		v |= 0X0F & nibble(name.charAt(i + 2));
		return (byte) v;
	}

	private static int nibble(char c) {
		c = Character.toUpperCase(c);
		return "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * Make sure stays sorted
	 */

	public static final String[] RESERVED_JAVA = {
			"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
			"default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto",
			"if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
			"private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
			"synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"
	};

	public static final String[] RESERVED_JSON = {
			"$ref"
	};
}
