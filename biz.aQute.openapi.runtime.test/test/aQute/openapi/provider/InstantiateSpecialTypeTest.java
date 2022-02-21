package aQute.openapi.provider;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import gen.instantiation.dates.InstantiationDates;

public class InstantiateSpecialTypeTest extends Assert {
	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	static class MyDates extends InstantiationDates.Dates {
		void foo() {

		}
	}

	@Test
	public void testOverridingInstantiating() throws Exception {
		class X extends InstantiationDates {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T instantiate_(Class<T> type) {
				if (type == Dates.class)
					return (T) new MyDates();

				return super.instantiate_(type);
			}

			@Override
			protected Dates putDates(Dates token) throws Exception {
				token.error = token instanceof MyDates ? "Yes!" : "Yuck " + token.getClass();
				return token;
			}

		}
		;
		rule.add(new X());

		String offset = rule.put("/v1/dates", "{\"error\":\"ok\", \"date\":\"2019-09-07\", \"dateTime\":\"1970-01-01T00:00:00Z\"}");
		assertEquals("{'date':'2019-09-07','dateTime':'1970-01-01T00:00:00Z','error':'Yes!'}", offset);
	}

}
