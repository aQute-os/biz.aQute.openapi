package aQute.openapi.provider;

import java.util.Optional;

import org.junit.Test;

import gen.validation.devicemanagementmodbusapi.ValidationDeviceManagementModbusApi;
import gen.validation.devicemanagementmodbusapi.ValidationDeviceManagementModbusApi.ModbusDeviceData;

public class ValidationTest {
	OpenAPIContext context = new OpenAPIContext(null, null, null, null) {
	};

	@Test
	public void testBasicValidationOk() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1;
		dd.unitId = 102;
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithMissingField() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithBadPattern() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "x192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1;
		dd.unitId = 102;
		dd.validate(context, "foo");
	}

	@Test(expected = OpenAPIBase.BadRequestResponse.class)
	public void testBasicValidationWithTooHighNumber() {
		ModbusDeviceData dd = new ValidationDeviceManagementModbusApi.ModbusDeviceData();
		dd.ip = "192.168.76.203";
		dd.mappingFileName = "foo";
		dd.port = 1;
		dd.unitId = 10000000;
		dd.name = Optional.of("Hello");
		dd.validate(context, "foo");
	}

}
