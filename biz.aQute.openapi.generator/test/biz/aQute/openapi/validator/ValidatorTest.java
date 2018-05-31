package biz.aQute.openapi.validator;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import aQute.openapi.annotations.Required;
import aQute.openapi.annotations.ValidatorString;
import aQute.openapi.validator.Validator;

public class ValidatorTest {

	public static class SimpleRequire {
		@Required
		public String				requiredString;
		@Required
		public Collection<String>	requiredCollection;
		@Required
		public String[]				requiredArray;
		@Required
		public int[]				requiredPrimitiveArray;
	}

	@Test
	public void testRequiredFieldsWithoutValues() {
		Validator validator = new Validator();
		validator.verify(new SimpleRequire());
		assertTrue(validator.check("required but not set"));
	}

	@Test
	public void testRequiredFieldsWithValues() {
		Validator validator = new Validator();
		SimpleRequire object = new SimpleRequire();
		object.requiredString = "Foo";
		object.requiredCollection = Arrays.asList("a");
		object.requiredArray = new String[] {};
		object.requiredPrimitiveArray = new int[3];

		validator.verify(object);
		assertTrue(validator.check());
	}

	public static class Patterned {

		@ValidatorString(pattern = "2\\.0\\.0")
		public String string;
	}

	@Test
	public void testPatternNotThere() {
		Validator validator = new Validator();
		validator.verify(new Patterned());
		assertTrue(validator.check());
	}

	@Test
	public void testCorrectPatern() {
		Validator validator = new Validator();
		Patterned p = new Patterned();
		p.string = "2.0.0";
		validator.verify(p);
		assertTrue(validator.check());
	}

	@Test
	public void testInCorrectPatern() {
		Validator validator = new Validator();
		Patterned p = new Patterned();
		p.string = "xxx";
		validator.verify(p);
		assertTrue(validator.check("#/string – Pattern mismatch"));
	}

	public static class WithList {
		public List<Patterned> p;
	}

	@Test
	public void testListPatternCorrect() {
		Validator validator = new Validator();
		WithList c = new WithList();
		Patterned p = new Patterned();
		p.string = "2.0.0";
		c.p = Arrays.asList(p);
		validator.verify(c);
		assertTrue(validator.check());
	}

	@Test
	public void testListPatternInCorrect() {
		Validator validator = new Validator();
		WithList c = new WithList();
		Patterned p = new Patterned();
		p.string = "xxx";
		c.p = Arrays.asList(p);
		validator.verify(c);
		assertTrue(validator.check("#/p/0/string – Pattern mismatch"));
	}

	public static class WithArray {
		public Patterned[] p;
	}

	@Test
	public void testArrayPatternCorrect() {
		Validator validator = new Validator();
		WithArray c = new WithArray();
		Patterned p = new Patterned();
		p.string = "2.0.0";
		c.p = new Patterned[] {
				p
		};
		validator.verify(c);
		assertTrue(validator.check());
	}

	@Test
	public void testArrayPatternInCorrect() {
		Validator validator = new Validator();
		WithArray c = new WithArray();
		Patterned p = new Patterned();
		p.string = "xxx";
		c.p = new Patterned[] {
				p
		};
		validator.verify(c);
		assertTrue(validator.check("#/p/0/string – Pattern mismatch"));
	}

	static public class ListWithPattern {
		@ValidatorString(pattern = "abc")
		public List<String> values;
	}

	@Test
	public void testListWithParentPattern() {
		Validator validator = new Validator();
		ListWithPattern lwp = new ListWithPattern();
		lwp.values = Arrays.asList("123", "abc", "def");
		validator.verify(lwp);
		assertTrue(validator.check("#/values/0 – Pattern mismatch. Value is 123",
				"#/values/2 – Pattern mismatch. Value is def"));
	}

	static public class MapWithPattern {
		@ValidatorString(pattern = "abc")
		public Map<String,String> values;
	}

	@Test
	public void testMapWithPattern() {
		Validator validator = new Validator();
		MapWithPattern lwp = new MapWithPattern();
		lwp.values = new HashMap<>();
		lwp.values.put("abc", "abc");
		lwp.values.put("def", "def");
		lwp.values.put("123", "123");

		validator.verify(lwp);
		assertTrue(validator.check("#/values/def – Pattern mismatch. Value is def",
				"#/values/123 – Pattern mismatch. Value is 123"));
	}
}
