package aQute.openapi.util;

public class ABNF {
	final long low, high;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ABNF[");
		for ( char c=0; c<128; c++) {
			if ( in(c))
				sb.append(c);
		}
		sb.append("]");
		return sb.toString();
	}

	public ABNF(String set,  ABNF... abnfs) {
		long l = 0, h = 0;
		for (int i = 0; i < set.length(); i++) {
			char c = set.charAt(i);
			if (c >= 0x80)
				throw new IllegalArgumentException("ABNF is about 7 bit ASCII, contains character " + c);

			if ( c < 64) {
				l |= (1L << c);
			} else {
				h |= (1L << (c - 64));
			}

		}
		for (ABNF abnf : abnfs) {
			l |= abnf.low;
			h |= abnf.high;
		}
		this.low = l;
		this.high = h;
	}

	public ABNF(ABNF... abnfs) {
		this("", abnfs);
	}


	public ABNF(long low, long high) {
		this.low = low;
		this.high = high;
	}

	public ABNF and(ABNF... abnfs) {
		long l = low, h = high;
		for (ABNF abnf : abnfs) {
			l &= abnf.low;
			h &= abnf.high;
		}
		return new ABNF(l, h);
	}

	public ABNF or(ABNF... abnfs) {
		long l = low, h = high;
		for (ABNF abnf : abnfs) {
			l |= abnf.low;
			h |= abnf.high;
		}
		return new ABNF(l, h);
	}

	public boolean in(char c) {
		if (c >= 0x80)
			return false;

		return get(c);
	}

	public boolean get(int c) {
		if (c < 64) {
			long bit = 1L << c;
			return (low & bit) != 0;
		}

		long bit = 1L << (c - 64);
		return (high & bit) != 0;
	}

}
