package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import gen.fieldcasing.FieldcasingBase;
import gen.fieldcasing.FieldcasingBase.Body;
import gen.fieldcasing.FieldcasingBase.Response;
import gen.validation.devicemanagementmodbusapi.ValidationDeviceManagementModbusApi;
import gen.validation.devicemanagementmodbusapi.ValidationDeviceManagementModbusApi.ModbusDeviceData;

public class ValidationTest {
	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	OpenAPIContext context = new OpenAPIContext(null, null, null, null) {
	};

	@Test
	public void testBasicValidationOk() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1L;
		dd.unitId = 102L;
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithMissingField() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.mappingFileName = "";
		dd.ip = "";
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithBadPattern() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "x192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1L;
		dd.unitId = 102L;
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithTooHighNumber() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1L;
		dd.unitId = 10000000L;
		dd.name = Optional.of("Hello");
		dd.validate(context, "foo");
	}
	
	
	@Test
	public void testActualCall() throws Exception {
		
		class Validation extends ValidationDeviceManagementModbusApi {

			@Override
			protected ModbusDeviceDataResponse addModbusDeviceToPlant(ModbusDeviceData device)
					throws Exception, BadRequestResponse {
				
				return null;
			}

			@Override
			protected ModbusDeviceDataResponse editModbusDeviceToPlant(ModbusDeviceData device)
					throws Exception, BadRequestResponse {
				return null;
			}

			@Override
			protected ModbusDeviceDataResponseList getModbusDevices() throws Exception {
				return null;
			}

			@Override
			protected MappingFileList getModbusMappingFiles() throws Exception {
				return null;
			}

			@Override
			protected void removeModbusDevice(String deviceId) throws Exception {
			}

		}
		Validation servlet = new Validation();
		rule.add(servlet);

		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1L;
		dd.unitId = 10L;

		TaggedData go = rule.http.build()
				.put()
				.asTag()
				.upload(dd)
				.go(rule.uri.resolve("/api/v1/devicemanagement/modbus/devices"));

		assertEquals(200,go.getResponseCode());

		dd.unitId=10000L;
		
		go = rule.http.build()
				.put()
				.asTag()
				.upload(dd)
				.go(rule.uri.resolve("/api/v1/devicemanagement/modbus/devices"));

		assertEquals(400,go.getResponseCode());
		
		servlet.validate = false;
		
		go = rule.http.build()
				.put()
				.asTag()
				.upload(dd)
				.go(rule.uri.resolve("/api/v1/devicemanagement/modbus/devices"));

		System.out.println(go);
		assertEquals(200,go.getResponseCode());

		dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = null;
		dd.mappingFileName = null;
		dd.port = null;
		dd.unitId = null;

		go = rule.http.build()
				.put()
				.asTag()
				.upload(dd)
				.go(rule.uri.resolve("/api/v1/devicemanagement/modbus/devices"));
		
		assertEquals(400,go.getResponseCode());
		
		servlet.require = false;
		go = rule.http.build()
				.put()
				.asTag()
				.upload(dd)
				.go(rule.uri.resolve("/api/v1/devicemanagement/modbus/devices"));
		
		assertEquals(200,go.getResponseCode());
		
	}
	

}
