package aQute.json.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import aQute.json.codec.JSONCodec;

public class OptionalTest extends Assert {
	static JSONCodec codec = new JSONCodec();

	static class OptionalType {
		public Optional<String> type = Optional.empty();

	}

	@Test
	public void testOptionalEncodeObject() throws Exception {
		OptionalType optionalType = new OptionalType();
		optionalType.type = Optional.of("foo");

		String string = codec.enc().put(optionalType).toString();
		assertEquals("{\"type\":\"foo\"}", string);

		optionalType.type = null;
		string = codec.enc().put(optionalType).toString();
		assertEquals("{}", string);

		optionalType.type = Optional.empty();
		string = codec.enc().put(optionalType).toString();
		assertEquals("{}", string);

		optionalType.type = Optional.ofNullable(null);
		string = codec.enc().put(optionalType).toString();
		assertEquals("{}", string);
	}

	@Test
	public void testOptionalDecodeObject() throws Exception {
		OptionalType optionalType = codec.dec().from("{}").get(OptionalType.class);
		assertNotNull(optionalType.type);
		assertFalse(optionalType.type.isPresent());

		optionalType = codec.dec().from("{\"type\":\"foo\"}").get(OptionalType.class);
		assertNotNull(optionalType.type);
		assertTrue(optionalType.type.isPresent());
		assertEquals("foo", optionalType.type.get());
	}

	static class OptionalArrayType {
		public List<Optional<String>> array;
	}

	@Test
	public void testOptionalDecodeArray() throws Exception {
		OptionalArrayType optionalType = codec.dec().from("{ \"array\": [\"foo\"] }").get(OptionalArrayType.class);
		assertNotNull(optionalType.array);
		assertTrue(optionalType.array.get(0).isPresent());

	}

	@Test
	public void testOptionalEncodeArray() throws Exception {
		OptionalArrayType x = new OptionalArrayType();
		x.array = new ArrayList<>();
		x.array.add(Optional.of("foo"));

		String s = codec.enc().put(x).toString();
		assertEquals("{\"array\":[\"foo\"]}", s);

	}

	static class Foo {
		public String bar;
	}

	static class OptionalArrayTypeFoo {
		public List<Optional<Foo>> array;
	}

	@Test
	public void testOptionalDecodeArrayFoo() throws Exception {
		OptionalArrayTypeFoo optionalType = codec.dec().from("{\"array\":[{\"bar\":\"bar\"}]}").get(OptionalArrayTypeFoo.class);
		assertNotNull(optionalType.array);
		assertTrue(optionalType.array.get(0).isPresent());
		assertEquals("bar", optionalType.array.get(0).get().bar);

	}

	@Test
	public void testOptionalEncodeArrayFoo() throws Exception {
		OptionalArrayTypeFoo x = new OptionalArrayTypeFoo();
		Foo foo = new Foo();
		foo.bar ="bar";

		x.array = new ArrayList<>();
		x.array.add(Optional.of(foo));

		String s = codec.enc().put(x).toString();
		assertEquals("{\"array\":[{\"bar\":\"bar\"}]}", s);

	}

	static class OptionalMapTypeFoo {
		public Map<String,Optional<Foo>> map;
	}

	@Test
	public void testOptionalDecodeMapFoo() throws Exception {
		OptionalMapTypeFoo optionalType = codec.dec().from("{\"map\":{\"foo\":{\"bar\":\"BAR\"}}}").get(OptionalMapTypeFoo.class);
		assertNotNull(optionalType.map);
		assertTrue(optionalType.map.get("foo").isPresent());
		assertEquals("BAR", optionalType.map.get("foo").get().bar);

	}

	@Test
	public void testOptionalEncodeMapFoo() throws Exception {
		OptionalArrayTypeFoo x = new OptionalArrayTypeFoo();
		Foo foo = new Foo();
		foo.bar ="bar";

		x.array = new ArrayList<>();
		x.array.add(Optional.of(foo));

		String s = codec.enc().put(x).toString();
		assertEquals("{\"array\":[{\"bar\":\"bar\"}]}", s);

	}
}
