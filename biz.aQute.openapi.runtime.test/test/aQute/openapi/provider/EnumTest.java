package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import aQute.json.codec.JSONCodec;
import osgi.enroute.dto.api.TypeReference;

public class EnumTest extends gen.enums.EnumsBase {

	@Override
	protected void enum$(AllEnumVariations body) throws Exception {
	}

	@Test
	public void testFieldsGenericTypes() throws Exception {
		Field simple = AllEnumVariations.class.getField("simple");
		assertEquals(simple.getGenericType(), SimpleEnum.class);

		Field array = AllEnumVariations.class.getField("array");
		assertEquals(array.getGenericType(), new TypeReference<List<ArrayEnum>>() {
		}.getType());

		Field optionalArray = AllEnumVariations.class.getField("optionalArray");
		assertEquals(optionalArray.getGenericType(), new TypeReference<Optional<List<OptionalArrayEnum>>>() {
		}.getType());

		Field optionalSimple = AllEnumVariations.class.getField("optionalSimple");
		assertEquals(optionalSimple.getGenericType(), new TypeReference<Optional<OptionalSimpleEnum>>() {
		}.getType());
	}

	@Test
	public void testSimpleSerialization() throws Exception {
		AllEnumVariations aev = new aQute.json.codec.JSONCodec().dec().from("{\"simple\":\"a\"}").get(AllEnumVariations.class);
		assertEquals(SimpleEnum.a, aev.simple);
	}

	@Test
	public void testArraySerialization() throws Exception {
		AllEnumVariations aev = new JSONCodec().dec().from("{\"array\":[\"e\",\"f\"]}").get(AllEnumVariations.class);
		assertEquals(Arrays.asList(ArrayEnum.e, ArrayEnum.f), aev.array);
	}

	@Test
	public void testOptionalSimpleSerialization() throws Exception {
		AllEnumVariations aev = new aQute.json.codec.JSONCodec().dec().from("{\"optionalSimple\":\"h\"}").get(AllEnumVariations.class);
		assertEquals(OptionalSimpleEnum.h, aev.optionalSimple.get());
	}

	@Test
	public void testOptionalArraySerialization() throws Exception {
		AllEnumVariations aev = new JSONCodec().dec().from("{\"optionalArray\":[\"k\",\"l\"]}").get(AllEnumVariations.class);
		assertEquals(Arrays.asList(OptionalArrayEnum.k, OptionalArrayEnum.l), aev.optionalArray.get());
	}

	@Test
	public void testWeirdMemberNamesEnum() throws Exception {
		AllEnumVariations aev = new JSONCodec().dec().from("{\"memberNames\":[\"new\",\"n^c\",\"m\",\"M\"]}").get(AllEnumVariations.class);
		assertEquals(Arrays.asList(MemberNamesEnum.new$, MemberNamesEnum.n$5Ec, MemberNamesEnum.m, MemberNamesEnum.M), aev.memberNames);
	}

}
