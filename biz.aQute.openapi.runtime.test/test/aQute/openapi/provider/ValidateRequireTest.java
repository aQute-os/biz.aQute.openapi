package aQute.openapi.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Test;

import aQute.openapi.provider.OpenAPIBase.BadRequestResponse;
import gen.validate_require.demo.Validate_requireDemo;
import gen.validate_require.demo.Validate_requireDemo.GeneralSettings;
import gen.validate_require.demo.Validate_requireDemo.MyListItem;
import gen.validate_require.demo.Validate_requireDemo.MyListItem2;
import gen.validate_require.demo.Validate_requireDemo.MySettings;
import gen.validate_require.demo.Validate_requireDemo.MySettings2;

public class ValidateRequireTest {
	OpenAPIContext context = new OpenAPIContext(null, null, null, null) {
	};

	@Test
	public void testBasicValidationOk() {
		MySettings gs = new Validate_requireDemo.MySettings();

		test(gs::validate, 400, "400:test general=null FAILS:  required but not set\n"
				+ "test myListWithoutAnyValidator=null FAILS:  required but not set\n"
				+ "test myDouble=null FAILS:  required but not set\n"
				+ "test myList=null FAILS:  required but not set\n"
				+ "test myListWithoutValidatorButValidatorInEntries=null FAILS:  required but not set");
		
		gs.general=new GeneralSettings();
		gs.myListWithoutAnyValidator=new ArrayList<>();
		MyListItem2 it1 = new MyListItem2();
		gs.myListWithoutAnyValidator.add( it1);
		gs.myDouble=3.14;
		gs.myList=new ArrayList<>();
		gs.myListWithoutValidatorButValidatorInEntries= new ArrayList<Validate_requireDemo.MyListItem>();
		
		test(gs::validate, 400, "400:test/general myDouble=null FAILS:  required but not set\n"
				+ "test/general id=null FAILS:  required but not set\n"
				+ "test/general myInt=null FAILS:  required but not set\n"
				+ "test/0/item_ myDouble=null FAILS:  required but not set\n"
				+ "test/0/item_ id=null FAILS:  required but not set\n"
				+ "test/0/item_ myInt=null FAILS:  required but not set\n"
				+ "test this.myList=[] FAILS: this.myList.size() >= 1");
		
		it1.myDouble=3.0d;
		it1.id="ab";
		it1.myInt=42L;
		
		gs.general.myDouble=1D;
		gs.general.id="generalId";
		gs.general.myInt=42L;
		gs.myList = new ArrayList<>();
		test(gs::validate, 400, "400:test this.myList=[] FAILS: this.myList.size() >= 1");
		
		MyListItem a = new MyListItem();
		gs.myList.add(a);
		test(gs::validate, 400, "400:test/0/item_ myDouble=null FAILS:  required but not set\n"
				+ "test/0/item_ id=null FAILS:  required but not set\n"
				+ "test/0/item_ myInt=null FAILS:  required but not set");

		a.myDouble = 30d;
		a.id="x";
		a.myInt=42L;
		test(gs::validate, 400, "400:test/0/item_ this.id=x FAILS: this.id.length() >= 3");

		a.id = "abc";
		gs.validate(context, "test");

		MyListItem it2 = new MyListItem();
		gs.myListWithoutValidatorButValidatorInEntries.add(it2);
		test(gs::validate, 400, "400:test/0/item_ myDouble=null FAILS:  required but not set\n"
				+ "test/0/item_ id=null FAILS:  required but not set\n"
				+ "test/0/item_ myInt=null FAILS:  required but not set");
		it2.myDouble=3.0d;
		it2.id="ab";
		it2.myInt=42L;
		
		test(gs::validate, 400, "400:test/0/item_ this.id=ab FAILS: this.id.length() >= 3");
		it2.id="abcd";
		
		gs.optList = Optional.of( new ArrayList<>());
		MyListItem it3 = new MyListItem();
		gs.optList.get().add(it3);
		test(gs::validate, 400, "400:test/0/item_ myDouble=null FAILS:  required but not set\n"
				+ "test/0/item_ id=null FAILS:  required but not set\n"
				+ "test/0/item_ myInt=null FAILS:  required but not set");
		it3.id="abcd";
		it3.myDouble=3.0d;
		it3.myInt=42L;
		gs.validate(context, "test");
		
		MySettings2 ms = new MySettings2();
		test(ms::validate, 400, "400:test myString=null FAILS:  required but not set\n"
				+ "test myDouble=null FAILS:  required but not set\n"
				+ "test myInt=null FAILS:  required but not set\n"
				+ "test myStringWithoutValidator=null FAILS:  required but not set");
		
		ms.myDouble=-3.0d;
		ms.myInt=0x10_0000L;
		ms.myString="a";
		ms.myStringWithoutValidator="abc";
		test(ms::validate, 400, "400:test this.myString=a FAILS: this.myString.length() >= 3\n"
				+ "test this.myDouble=-3.0 FAILS: this.myDouble >= 0.0\n"
				+ "test this.myInt=1048576 FAILS: this.myInt <= 65535");
		ms.myString="abc";
		ms.myDouble=3.0d;
		ms.myInt=423L;
		ms.validate(context, "test");
		
	}

	private void test(BiConsumer<OpenAPIContext,String> exec, int httpCode, String string) {
		try {
			OpenAPIContext context = new OpenAPIContext(null, null, null, null) {
			};
			exec.accept(context, "test");
			fail("could validate");
		} catch (BadRequestResponse r) {
			assertThat(r.resultCode).isEqualTo(httpCode);
			assertThat(r.getMessage()).isEqualTo(string);
		} finally {
		}
	}

}

