package aQute.openapi.provider;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WWWUtils {
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

	public static void properties(StringBuilder sb, Map<String,String> parameters) {
		for (Map.Entry<String,String> e : parameters.entrySet()) {
			property(sb, e.getKey(), e.getValue());
		}
	}

}
