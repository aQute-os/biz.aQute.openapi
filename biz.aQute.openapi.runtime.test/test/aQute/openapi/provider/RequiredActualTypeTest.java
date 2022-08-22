package aQute.openapi.provider;

import java.net.URI;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import biz.aQute.openapi.runtime.test.requiredactualtype.ActualTypeTest;
import biz.aQute.openapi.runtime.test.requiredactualtype.EnumActualTypeTest;
import biz.aQute.openapi.runtime.test.requiredactualtype.EnumWrapperActualTypeTest;
import gen.requiredactualtype.RequiredactualtypeBase;

public class RequiredActualTypeTest extends Assert {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void getRequiredActualType() throws Exception {
		
		ActualTypeTest requiredActualType = new ActualTypeTest();
		requiredActualType.id = "parent";
		requiredActualType.number = 12.3;
		requiredActualType.children = new ArrayList<>();
		
		for (int i = 0; i < 5; i++) {
			ActualTypeTest c = new ActualTypeTest();
			requiredActualType.id = "child" + i;
			requiredActualType.number = 98.32;
			requiredActualType.children.add(c);
		}
		
		EnumWrapperActualTypeTest enumWrapper = new EnumWrapperActualTypeTest();
		enumWrapper.season = EnumActualTypeTest.SUMMER;
	
		class X extends RequiredactualtypeBase {

			@Override
			public ActualTypeTest getRequiredActualType() {
				return requiredActualType;
			}
			
			@Override
			public EnumWrapperActualTypeTest getEnumType() {
				return enumWrapper;
			}
			
		}
		
		rule.add(new X());
		
		URI recursiveDataURI = rule.uri.resolve("/requiredactualtype/recursive");
		ActualTypeTest returnedData = rule.http.build().get().get(ActualTypeTest.class).go(recursiveDataURI);
		assertEquals(returnedData.id, requiredActualType.id);
		Assertions.assertThat(returnedData.children).hasSameSizeAs(requiredActualType.children);
		
		assertTrue(returnedData.children.size() == requiredActualType.children.size());
		
		URI enumDataURI = rule.uri.resolve("/requiredactualtype/enum");
		EnumWrapperActualTypeTest returnedEnumWrapper = rule.http.build().get().get(EnumWrapperActualTypeTest.class).go(enumDataURI);
		
		Assertions.assertThat(returnedEnumWrapper.season).isEqualTo(enumWrapper.season);
		
	}
}
