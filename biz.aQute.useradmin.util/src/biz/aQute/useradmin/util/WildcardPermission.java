package biz.aQute.useradmin.util;

public abstract class WildcardPermission {

	public static WildcardPermission caseSensitive(String s) {
		if (isValid(s) != null)
			throw new IllegalArgumentException(isValid(s));

		if (isWildcard(s))
			return new CaseSensitiveWildcard(s);
		else
			return new CaseSensitiveLiteral(s);

	}

	public static WildcardPermission ignoreCase(String s) {
		if (isValid(s) != null)
			throw new IllegalArgumentException(isValid(s));
		if (isWildcard(s))
			return new CaseInSensitiveWildcard(s);
		else
			return new CaseInSensitiveLiteral(s);

	}

	public static class CaseSensitiveLiteral extends WildcardPermission {
		final String permission;

		public CaseSensitiveLiteral(String s) {
			this.permission = s;
		}

		public boolean implies(String target) {
			return permission == target || permission.equals(target);
		}
	}

	public static class CaseInSensitiveLiteral extends WildcardPermission {
		final String permission;

		public CaseInSensitiveLiteral(String s) {
			this.permission = s;
		}

		public boolean implies(String target) {
			return permission == target || permission.equals(target) || permission.equalsIgnoreCase(target);
		}
	}

	public static class CaseSensitiveWildcard extends WildcardPermission {
		final char[]	permission;
		final boolean	suffixMatch;

		public CaseSensitiveWildcard(String s) {
			this.permission = s.toCharArray();
			int l = permission.length - 2;
			suffixMatch = l >= 0 && permission[l] == '*' && permission[l + 1] == '*';
			if (suffixMatch) {
				permission[l] = 0x01;
			}
		}

		public boolean implies(String target) {

			assert target != null;
			assert target.length() > 0;

			class Matcher {
				int	t		= 0;
				int	last	= 0;

				boolean match() {
					int length = target.length();
					boolean ok = true;
					char pc;
					char tc;

					for (int p = 0; p < permission.length; p++) {

						pc = permission[p];

						tc = t < length ? target.charAt(t) : 0;
						if ( tc == '\\') {
							t++;
							tc = t < length ? target.charAt(t) : 0;
						}
						switch (pc) {
						case 0x01: // double wildcard
							return true;

						case '*':
							skipTargetPart();
							break;

						case ':':
							if (!ok)
								return false;

							boolean endOfPart = tc == ':' || tc == 0;
							if (!endOfPart)
								return false;

							t = last = t + 1;
							break;

						case ',':
							if (ok) {
								while (p < permission.length && permission[p] != ':')
									p++;
								p--;
							} else {
								ok = true;
								t = last;
							}
							break;

						case '\\':
							p++;
							if (p < permission.length)
								pc = permission[p];
							else
								pc = 0;

							// FALL THROUGH

						default:
							ok &= pc == tc || isEqual(pc, tc);
							t++;
							break;
						}
					}
					return ok;
				}

				private void skipTargetPart() {
					while (t < target.length() && target.charAt(t) != ':') {
						if ( target.charAt(t)=='\\')
							t++;
						t++;
					}
					last = t;
				}
			}
			return new Matcher().match();
		}

		boolean isEqual(char a, char b) {
			return a == b;
		}
	}

	static class CaseInSensitiveWildcard extends CaseSensitiveWildcard {

		public CaseInSensitiveWildcard(String s) {
			super(s.toUpperCase());
		}

		boolean isEqual(char a, char b) {
			return a == b || a == elevateCase(b);
		}

		// With thanks to
		// https://stackoverflow.com/questions/10223176/how-to-compare-character-ignoring-case-in-primitive-types
		char elevateCase(char c) {
			if (c < 0x130 || c > 0x212B)
				return Character.toUpperCase(c);

			if (c == 0x130 || c == 0x3F4 || c == 0x2126 || c >= 0x212A)
				return Character.toUpperCase(Character.toLowerCase(c));

			return Character.toUpperCase(c);
		}
	}

	public abstract boolean implies(String target);

	public static boolean isWildcard(String s) {
		return s.indexOf('*') >= 0 || s.indexOf(',') >= 0 || s.indexOf('\\') > 0;
	}

	enum State {
		BEGIN, WC, WCC, TEXT;
	}

	public static String isValid(String permission) {
		State state = State.BEGIN;
		for (int p = 0; p < permission.length(); p++) {
			char pc = permission.charAt(p);
			switch (pc) {
			case '*':
				switch (state) {
				case BEGIN:
					state = State.WC;
					break;

				case WC:
					state = State.WCC;
					break;
				case WCC:
					return "Max 2 wildcards in a row";

				case TEXT:
				default:
					return "A wildcard must not be preceded with " + state;
				}
				break;

			case ':':
				switch (state) {
				case BEGIN:
					return "Empty part";

				case WCC:
					return "A double wildcard must be the last part";

				case TEXT:
				case WC:
				default:
					state = State.BEGIN;
					break;
				}
				break;

			case ',':
				switch (state) {
				case BEGIN:
					return "An option must not be empty";

				case WC:
				case WCC:
					return "A wildcard must be followed by a ':' or end of string";

				case TEXT:
				default:
					state = State.BEGIN;
					break;
				}
				break;

			case '\\':
				p++;
				if (p < permission.length())
					pc = permission.charAt(p);
				else
					return "Ends with a single backslash";

				// FALL THROUGH

			default:
				switch (state) {
				case WC:
				case WCC:
					return "A wildcard must not be followed by text";

				case BEGIN:
				case TEXT:
				default:
					state = State.TEXT;
					break;
				}
				break;
			}
		}
		if (state == State.BEGIN) {
			return "Last part is empty";
		}
		return null;
	}

}
