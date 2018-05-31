package aQute.openapi.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import aQute.lib.converter.Converter;

public class WWWUtils {

	public final static String		APPLICATION_X_WWW_FORM_URLENCODED	= "application/x-www-form-urlencoded";
	public final static String		MULTIPART_FORM_DATA					= "multipart/form-data";

	private static final String[]	EMPTY_STRING_ARRAY					= new String[0];
	static ABNF						unreserved							= new ABNF(
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.~-");
	// static ABNF sub_delims = new ABNF("!$&'()*+,;=");
	// static ABNF pchar = new ABNF(":@%", unreserved, sub_delims);
	static ABNF						query								= new ABNF("/?", unreserved);

	/**
	 * Most HTTP header field values are defined using common syntax components
	 * (token, quoted-string, and comment) separated by whitespace or specific
	 * delimiting characters. Delimiters are chosen from the set of US-ASCII
	 * visual characters not allowed in a token (DQUOTE and "(),/:;<=>?@[\]{}").
	 *
	 * <pre>
	     token          = 1*tchar
	
	     tchar          = "!" / "#" / "$" / "%" / "&" / "'" / "*"
	                    / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~"
	                    / DIGIT / ALPHA
	                    ; any VCHAR, except delimiters
	 * </pre>
	 *
	 * A string of text is parsed as a single value if it is quoted using
	 * double-quote marks.
	 *
	 * <pre>
	     quoted-string  = DQUOTE *( qdtext / quoted-pair ) DQUOTE
	     qdtext         = HTAB / SP /%x21 / %x23-5B / %x5D-7E / obs-text
	     obs-text       = %x80-FF
	 * </pre>
	 *
	 * Comments can be included in some HTTP header fields by surrounding the
	 * comment text with parentheses. Comments are only allowed in fields
	 * containing "comment" as part of their field value definition. comment =
	 * "(" *( ctext / quoted-pair / comment ) ")" ctext = HTAB / SP / %x21-27 /
	 * %x2A-5B / %x5D-7E / obs-text The backslash octet ("\") can be used as a
	 * single-octet quoting mechanism within quoted-string and comment
	 * constructs. Recipients that process the value of a quoted-string MUST
	 * handle a quoted-pair as if it were replaced by the octet following the
	 * backslash. quoted-pair = "\" ( HTAB / SP / VCHAR / obs-text ) A sender
	 * SHOULD NOT generate a quoted-pair in a quoted-string except where
	 * necessary to quote DQUOTE and backslash octets occurring within that
	 * string. A sender SHOULD NOT generate a quoted-pair in a comment except
	 * where necessary to quote parentheses ["(" and ")"] and backslash octets
	 * occurring within that comment.
	 *
	 * @param sb
	 * @param value
	 */
	public static void quoted_string(StringBuilder sb, String value) {
		sb.append("\"");
		escape(sb, value);
		sb.append("\"");
	}

	public static void escape(StringBuilder sb, String value) {
		byte[] data = value.getBytes(StandardCharsets.UTF_8);
		for (int i = 0; i < data.length; i++) {
			char c = (char) data[i];

			if (isEscaped(c))
				sb.append("\\");
			sb.append(c);
		}
	}

	public static boolean isEscaped(char c) {
		boolean escaped = c == '\t' || c < 0x20 || c == '"' || c == '\\' || c >= 0x7F;
		return escaped;
	}

	public static void property(StringBuilder sb, String key, String value) {
		if (value != null) {
			sb.append(" ");
			escape(sb, key);
			sb.append("=");
			quoted_string(sb, value);
		}
	}

	public static void properties(StringBuilder sb, Map<String, String> parameters) {
		for (Map.Entry<String, String> e : parameters.entrySet()) {
			property(sb, e.getKey(), e.getValue());
		}
	}

	public static String encodeParameter(String unencoded) {
		for (int i = 0; i < unencoded.length(); i++) {
			char c = unencoded.charAt(i);
			if (!query.in(c)) {
				return encodeParameter(unencoded, i);
			}
		}
		return unencoded;
	}

	private static String encodeParameter(String unencoded, int i) {
		byte data[] = unencoded.getBytes(StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder(unencoded.length() + 10);
		for (int j = 0; j < i; j++) {
			sb.append((char) data[j]);
		}
		while (i < data.length) {
			char c = (char) data[i++];

			if (query.in(c)) {
				sb.append(c);
			} else {
				hexEncode(sb, c);
			}
		}
		return sb.toString();
	}

	public static void hexEncode(StringBuilder sb, char c) {
		sb.append("%");
		sb.append(nibble(c >> 4));
		sb.append(nibble(c));
	}

	public static char nibble(int n) {
		n = n & 0xF;
		switch (n) {
		case 0:
			return '0';
		case 1:
			return '1';
		case 2:
			return '2';
		case 3:
			return '3';
		case 4:
			return '4';
		case 5:
			return '5';
		case 6:
			return '6';
		case 7:
			return '7';
		case 8:
			return '8';
		case 9:
			return '9';
		case 10:
			return 'A';
		case 11:
			return 'B';
		case 12:
			return 'C';
		case 13:
			return 'D';
		case 14:
			return 'E';
		case 15:
		default:
			return 'F';
		}
	}

	/**
	 * Compare without leaking match length through timing
	 *
	 * @param a
	 * @param b
	 * @return true if equal, false if not
	 */
	static public boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}

	public static boolean isEncrypted(HttpServletRequest request) {
		return "https".equalsIgnoreCase(request.getScheme());
	}

	public static String basic(String clientId, String clientSecret) {
		return "Basic "
				+ Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
	}

	public static Map<String, String[]> parameters(URI uri) {
		return parameters(uri.getRawQuery());
	}

	public static Map<String, String[]> parameters(String rawQuery) {
		if (query == null)
			return Collections.emptyMap();

		Map<String, String[]> map = new HashMap<>();
		String qs[] = rawQuery.split("&");
		for (String q : qs) {
			String parts[] = q.split("=");
			String key = unencode(parts[0]);
			String value = parts.length > 1 ? unencode(parts[1]) : null;
			String[] previous = map.getOrDefault(key, EMPTY_STRING_ARRAY);
			map.put(key, merge(value, previous));
		}
		return map;
	}

	public static String[] merge(String value, String[] previous) {
		String[] next = new String[previous.length + 1];
		next[0] = value;
		System.arraycopy(previous, 0, next, 1, previous.length);
		return next;
	}

	public static String unencode(String string) {
		System.out.println(query);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (query.in(c))
				bout.write(c);
			else if (c == '%') {
				if (i < string.length() + 2) {
					c = fromHexCharacters(string.charAt(i + 1), string.charAt(i + 2));
					bout.write(c);
					i += 2;
				} else {
					throw new IllegalArgumentException(
							"Invalid query string, not enough characters after % sign: " + string);
				}
			} else
				throw new IllegalArgumentException("Unknown character " + c + " in query string: " + string);
		}
		return new String(bout.toByteArray(), StandardCharsets.UTF_8);
	}

	public static char fromHexCharacters(char first, char second) {
		return (char) (nibble(first) * 16 + nibble(second));
	}

	public static int nibble(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';

		if (c >= 'A' && c <= 'Z')
			return c - 'A' + 10;
		if (c >= 'a' && c <= 'z')
			return c - 'a' + 10;

		throw new IllegalArgumentException("Invalid hex character " + c);
	}

	public static <T> T parameters(Class<T> clazz, String content) throws Exception {
		String parts[] = content.split("&");
		T newInstance = clazz.newInstance();
		for (String part : parts) {
			String ass[] = part.split("=");
			String key = unencode(ass[0]);
			if (ass.length == 2) {
				try {
					String value = unencode(ass[1]);
					Field field = clazz.getField(key);
					Object converted = Converter.cnv(field.getGenericType(), value);
					field.set(newInstance, converted);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException e) {
					// ignore
				}
			}
		}
		return newInstance;
	}

	public static String shorten(String string, int i) {
		if (i < 3)
			throw new IllegalArgumentException("Require at least 3 characters in output");

		if (string.length() < i)
			return string;

		i -= 3;
		return string.substring(0, i / 2) + "..." + string.substring(string.length() - i / 2);
	}

	public static Optional<String[]> parseAuthorizaton(String auth) {
		String pair[] = new String[2];

		if (auth != null && auth.toLowerCase().startsWith("basic")) {

			String base64Credentials = auth.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials),
					StandardCharsets.UTF_8);

			final String[] values = credentials.split(":", 2);
			if (values.length == 2) {
				pair[0] = values[0];
				pair[1] = values[1];
				return Optional.of(pair);
			}
		}
		return Optional.empty();
	}
}
