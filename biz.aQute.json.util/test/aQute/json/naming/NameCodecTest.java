package aQute.json.naming;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Test;

public class NameCodecTest {

	@Test
	public void testNameEncoding() {
		assertEquals("abc", NameCodec.encode("abc"));
		assertEquals("abc$20def", NameCodec.encode("abc def"));
		assertEquals("abc def", NameCodec.decode("abc$20def"));
		assertEquals("abc$E28692def", NameCodec.encode("abc\u2192def"));
		assertEquals("abc$C2ACdef", NameCodec.encode("abc\u00ACdef"));
		assertEquals("abc\u00ACdef", NameCodec.decode("abc$C2ACdef"));
		assertEquals("abc\u2192def", NameCodec.decode("abc$E28692def"));

		String s = "_" + new String(new int[] { 0x1F721 }, 0, 1) + "_";
		assertEquals("_$F09F9CA1_", NameCodec.encode(s));
		assertEquals(s, NameCodec.decode(s));
	}

	static class Names {
		@Name("FOO")
		public String	foo;

		public String	bar;

	}

	@Test
	public void testFieldNameEncodingWithAnnotationOverride() throws NoSuchFieldException, SecurityException {
		Field foo = Names.class.getField("foo");
		assertEquals("FOO", NameCodec.decode(foo));
		Field bar = Names.class.getField("bar");
		assertEquals("bar", NameCodec.decode(bar));
	}

	@Test
	public void testReservedNames() throws NoSuchFieldException, SecurityException {
		assertEquals("new$", NameCodec.encode("new"));
		assertEquals("new", NameCodec.decode("new$"));
	}

	@Test
	public void testUnencodedNames() {
		assertEquals("$ref", NameCodec.decode("$ref"));
	}
}
