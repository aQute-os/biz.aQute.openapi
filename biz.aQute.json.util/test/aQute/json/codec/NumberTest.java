package aQute.json.codec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class NumberTest {
	JSONCodec codec = new JSONCodec();

	@Test
	public void testIntegerWithOverflow() throws Exception {
		test("123e3", 123_000);
	}
	
	@Test
	public void testBasic() throws Exception {
		test("123", 123f);
		test("123.0", 123f);
		test("123.0e2", 12300f);
		test("123", (byte)123);
		test("123.0", (byte)123);
		test("123.0e-1", (byte)12);
		test("123", (short)123);
		test("123.0", (short)123);
		test("123.0e2", (short)12300);
		test("123", 123);
		test("123.0", 123);
		test("123.0e3", 123_000);
		test("123", 123L);
		test("123.0", 123L);
		test("123.0e6", 123_000_000L);
		test("123", 123d);
		test("123.0", 123d);
		test("123.0e100", 123e100d);
		test("-123", (byte)-123);
		test("-123", (short)-123);
		test("-123.0", (short)-123);
		test("-123", -123);
		test("-123", -123L);
		test("-123", -123f);
		test("-123", -123d);
		test("+123", (byte)123);
		test("+123", (short)123);
		test("+123", 123);
		test("+123", 123L);
		test("+123", 123f);
		test("+123", 123d);
		
		
	}
	
	@Test
	public void testExponents() throws Exception {
		test("1000e-3", (byte)1);
		test("1000000e+3", 1_000_000_000);
		test("1000000e+6", 1_000_000_000_000L);
		test("123.123e3", 123_123L);
		test("123.123", 123.123f);
		test("123.123e3", 123_123f);
		test("123.123E3", 123_123f);
		test("123.123E+3", 123_123f);
		test("123.123E-3", 0.123_123f);
	}
	@Test
	public void testOverflowMantissa() throws Exception {
		test("1000000000000000000000", 1_000_000_000_000_000_000_000D);
		test("1000000000000000000000.01", 1_000_000_000_000_000_000_000.01D);
		test("-1000000000000000000000.01", -1_000_000_000_000_000_000_000.01D);
	}

	
	@Test
	public void startWithZero() throws Exception {
		test("0", 0L);
		test("0.1", 0.1D);
		test("0.001", 0.001D);
		test("0.001", 0.001f);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNotANumber() throws Exception {
		test("X", 0L);
		
	}

	@Test
	public void testDefaultTypeDouble() throws Exception {
		Decoder dec = codec.dec().from("1.0");
		Number n = codec.parseNumber(dec, null);
		assertThat(n).isEqualTo(1D);
	}

	@Test
	public void testDefaultTypeInteger() throws Exception {
		Decoder dec = codec.dec().from("1");
		Number n = codec.parseNumber(dec, null);
		assertThat(n).isEqualTo(1);
	}
	
	@Test
	public void testRanges() throws Exception {
		long l = Integer.MAX_VALUE + 1000L;
		testExpected( Long.toString(l), 0, IllegalArgumentException.class);
		testExpected( Long.toString(l), (byte)0, IllegalArgumentException.class);
		testExpected( Long.toString(l), (short)0, IllegalArgumentException.class);
		testExpected( Long.toString(l), (int)0, IllegalArgumentException.class);
		
		
		testExpected( "1e40", 0L, IllegalArgumentException.class);
	}

	private void testExpected(String string, Object l, Class<? extends Exception> class1) throws Exception {
		try {
			test( string, l);
			fail("Expected exception " + class1.getSimpleName());
		} catch( Exception e) {
			if ( class1.isInstance(e)) {
				System.out.println(e);
				return;
			}
			throw e;
		}
	}
	
	private void test(String s, Object v) throws Exception {
		Decoder dec = codec.dec().strict().from(s);
		Number n = codec.parseNumber(dec, v.getClass());
		assertThat(n).isEqualTo(v);

	}
}
