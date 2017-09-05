package gen.validation.devicemanagementmodbusapi;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * <li>{@link #addModbusDeviceToPlant(ModbusDeviceData) POST /devicemanagement/modbus/devices =  AddModbusDeviceToPlant}
 * 
 * <li>{@link #editModbusDeviceToPlant(ModbusDeviceData) PUT /devicemanagement/modbus/devices =  EditModbusDeviceToPlant}
 * 
 * <li>{@link #getModbusDevices() GET /devicemanagement/modbus/devices =  GetModbusDevices}
 * 
 * <li>{@link #getModbusMappingFiles() GET /devicemanagement/modbus/mappingFiles =  GetModbusMappingFiles}
 * 
 * <li>{@link #removeModbusDevice(String) DELETE /devicemanagement/modbus/devices/<b>[deviceId]</b> =  RemoveModbusDevice}
 * 
 * </ul>
 * 
 */

@RequireValidationDeviceManagementModbusApi
public abstract class ValidationDeviceManagementModbusApi extends OpenAPIBase {

public static final String BASE_PATH = "/api/v1";

/**
 * 
 * POST /devicemanagement/modbus/devices = AddModbusDeviceToPlant
 * 
 * Adds the specified modbus devices to the plant.
 * 
 * @param device –  (body) collectionFormat=%scsv
 * 
   * @returns 200 / registered device data
   * @returns 400 / Bad Request
   * @returns 500 / Internal Server Error
 * 200
 * 
 * 400
 * 
 * 500
 * 
 */

protected abstract ModbusDeviceDataResponse addModbusDeviceToPlant(ModbusDeviceData device) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * PUT /devicemanagement/modbus/devices = EditModbusDeviceToPlant
 * 
 * Adds the specified modbus devices to the plant.
 * 
 * @param device –  (body) collectionFormat=%scsv
 * 
   * @returns 200 / registered device data
   * @returns 400 / Bad Request
   * @returns 500 / Internal Server Error
 * 200
 * 
 * 400
 * 
 * 500
 * 
 */

protected abstract ModbusDeviceDataResponse editModbusDeviceToPlant(ModbusDeviceData device) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /devicemanagement/modbus/devices = GetModbusDevices
 * 
 * Return a list registered modbus devices.
 * 
   * @returns 200 / device list
   * @returns 500 / Internal Server Error
 * 200
 * 
 * 500
 * 
 */

protected abstract ModbusDeviceDataResponseList getModbusDevices() throws Exception;

/**
 * 
 * GET /devicemanagement/modbus/mappingFiles = GetModbusMappingFiles
 * 
   * @returns 200 / list of mapping file names
   * @returns 500 / Internal Server Error
 * 200
 * 
 * 500
 * 
 */

protected abstract MappingFileList getModbusMappingFiles() throws Exception;

/**
 * 
 * DELETE /devicemanagement/modbus/devices/{deviceId} = RemoveModbusDevice
 * 
 * Remove the specified modbus devices from the plant.
 * 
 * @param deviceId – Identifier for the device. (path) collectionFormat=%scsv
 * 
   * @returns 200 / Ok
   * @returns 404 / Not found
   * @returns 500 / Internal Server Error
 * 200
 * 
 * 404
 * 
 * 500
 * 
 */

protected abstract void removeModbusDevice(String deviceId) throws Exception;

/**
 * 
 * ModbusDeviceDataResponse
 * 
 * Data for modbus device creation
 * 
 */

public static class ModbusDeviceDataResponse extends OpenAPIBase.DTO {

    public Optional<String> mappingFileName = Optional.empty();
    public long port;
    public String ip;
    public String name;
    public long unitId;
    public String deviceId;

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  (this.mappingFileName.isPresent() ) {
    context.validate(this.mappingFileName.get().length() >= 3, this.mappingFileName.get(), "this.mappingFileName.get()", "this.mappingFileName.get().length() >= 3");
    context.validate(this.mappingFileName.get().length() <= 32, this.mappingFileName.get(), "this.mappingFileName.get()", "this.mappingFileName.get().length() <= 32");
       }
    context.validate(this.port >= 0, this.port, "this.port", "this.port >= 0");
    context.validate(this.port <= 65535, this.port, "this.port", "this.port <= 65535");
       if  ( context.require(this.ip, "this.ip") ) {
    context.validate(this.ip.matches("\\b[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"), this.ip, "this.ip", "this.ip.matches(\"\\\\b[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\")");
       }
       if  ( context.require(this.name, "this.name") ) {
    context.validate(this.name.length() >= 3, this.name, "this.name", "this.name.length() >= 3");
    context.validate(this.name.length() <= 32, this.name, "this.name", "this.name.length() <= 32");
       }
    context.validate(this.unitId >= 0, this.unitId, "this.unitId", "this.unitId >= 0");
    context.validate(this.unitId <= 127, this.unitId, "this.unitId", "this.unitId <= 127");
     context.end();
    }
    public ModbusDeviceDataResponse mappingFileName(String mappingFileName){ this.mappingFileName=Optional.ofNullable(mappingFileName); return this; }
    public Optional<String> mappingFileName(){ return this.mappingFileName; }

    public ModbusDeviceDataResponse port(long port){ this.port=port; return this; }
    public long port(){ return this.port; }

    public ModbusDeviceDataResponse ip(String ip){ this.ip=ip; return this; }
    public String ip(){ return this.ip; }

    public ModbusDeviceDataResponse name(String name){ this.name=name; return this; }
    public String name(){ return this.name; }

    public ModbusDeviceDataResponse unitId(long unitId){ this.unitId=unitId; return this; }
    public long unitId(){ return this.unitId; }

    public ModbusDeviceDataResponse deviceId(String deviceId){ this.deviceId=deviceId; return this; }
    public String deviceId(){ return this.deviceId; }

}

/**
 * 
 * ModbusDeviceData
 * 
 * Data for modbus device creation
 * 
 */

public static class ModbusDeviceData extends OpenAPIBase.DTO {

    public String mappingFileName;
    public long port;
    public String ip;
    public Optional<String> name = Optional.empty();
    public long unitId;

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  ( context.require(this.mappingFileName, "this.mappingFileName") ) {
    context.validate(this.mappingFileName.length() >= 3, this.mappingFileName, "this.mappingFileName", "this.mappingFileName.length() >= 3");
    context.validate(this.mappingFileName.length() <= 32, this.mappingFileName, "this.mappingFileName", "this.mappingFileName.length() <= 32");
       }
    context.validate(this.port >= 0, this.port, "this.port", "this.port >= 0");
    context.validate(this.port <= 65535, this.port, "this.port", "this.port <= 65535");
       if  ( context.require(this.ip, "this.ip") ) {
    context.validate(this.ip.matches("\\b[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"), this.ip, "this.ip", "this.ip.matches(\"\\\\b[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\")");
       }
       if  (this.name.isPresent() ) {
    context.validate(this.name.get().length() >= 3, this.name.get(), "this.name.get()", "this.name.get().length() >= 3");
    context.validate(this.name.get().length() <= 32, this.name.get(), "this.name.get()", "this.name.get().length() <= 32");
       }
    context.validate(this.unitId >= 0, this.unitId, "this.unitId", "this.unitId >= 0");
    context.validate(this.unitId <= 127, this.unitId, "this.unitId", "this.unitId <= 127");
     context.end();
    }
    public ModbusDeviceData mappingFileName(String mappingFileName){ this.mappingFileName=mappingFileName; return this; }
    public String mappingFileName(){ return this.mappingFileName; }

    public ModbusDeviceData port(long port){ this.port=port; return this; }
    public long port(){ return this.port; }

    public ModbusDeviceData ip(String ip){ this.ip=ip; return this; }
    public String ip(){ return this.ip; }

    public ModbusDeviceData name(String name){ this.name=Optional.ofNullable(name); return this; }
    public Optional<String> name(){ return this.name; }

    public ModbusDeviceData unitId(long unitId){ this.unitId=unitId; return this; }
    public long unitId(){ return this.unitId; }

}

/**
 * 
 * MappingFileName
 * 
 */

public static class MappingFileName extends OpenAPIBase.DTO {

    public String name;

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  ( context.require(this.name, "this.name") ) {
    context.validate(this.name.length() >= 3, this.name, "this.name", "this.name.length() >= 3");
    context.validate(this.name.length() <= 32, this.name, "this.name", "this.name.length() <= 32");
       }
     context.end();
    }
    public MappingFileName name(String name){ this.name=name; return this; }
    public String name(){ return this.name; }

}

/**
 * 
 * ModbusDeviceDataResponseList
 * 
 * The list of modbus ModbusDeviceData to create the devices.
 * 
 */

public static class ModbusDeviceDataResponseList extends OpenAPIBase.DTO {

    public Optional<List<ModbusDeviceDataResponse>> modbusDeviceDataResponseList = Optional.empty();

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  (this.modbusDeviceDataResponseList.isPresent() ) {
    int counter_=0;
    for( ModbusDeviceDataResponse item_ : this.modbusDeviceDataResponseList.get()) {
        context.begin(counter_++);
       if  ( context.require(item_, "item_") ) {
       item_.validate(context, "item_");
       }
        context.end();
    }
       }
     context.end();
    }
    public ModbusDeviceDataResponseList modbusDeviceDataResponseList(List<ModbusDeviceDataResponse> modbusDeviceDataResponseList){ this.modbusDeviceDataResponseList=Optional.ofNullable(modbusDeviceDataResponseList); return this; }
    public Optional<List<ModbusDeviceDataResponse>> modbusDeviceDataResponseList(){ return this.modbusDeviceDataResponseList; }

}

/**
 * 
 * MappingFileList
 * 
 * The mapping file list.
 * 
 */

public static class MappingFileList extends OpenAPIBase.DTO {

    public Optional<List<MappingFileName>> mappingFiles = Optional.empty();

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  (this.mappingFiles.isPresent() ) {
    int counter_=0;
    for( MappingFileName item_ : this.mappingFiles.get()) {
        context.begin(counter_++);
       if  ( context.require(item_, "item_") ) {
       item_.validate(context, "item_");
       }
        context.end();
    }
       }
     context.end();
    }
    public MappingFileList mappingFiles(List<MappingFileName> mappingFiles){ this.mappingFiles=Optional.ofNullable(mappingFiles); return this; }
    public Optional<List<MappingFileName>> mappingFiles(){ return this.mappingFiles; }

}

  /*****************************************************************/

  public ValidationDeviceManagementModbusApi() {
    super(BASE_PATH,gen.validation.ValidationBase.class,
         "AddModbusDeviceToPlant POST   /devicemanagement/modbus/devices  PAYLOAD ModbusDeviceData  RETURN ModbusDeviceDataResponse",
         "EditModbusDeviceToPlant PUT    /devicemanagement/modbus/devices  PAYLOAD ModbusDeviceData  RETURN ModbusDeviceDataResponse",
         "GetModbusDevices     GET    /devicemanagement/modbus/devices  RETURN ModbusDeviceDataResponseList",
         "GetModbusMappingFiles GET    /devicemanagement/modbus/mappingFiles  RETURN MappingFileList",
         "RemoveModbusDevice   DELETE /devicemanagement/modbus/devices/{deviceId}");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "devicemanagement".equals(segments[index])) {
      index++;

      if( index < segments.length && "modbus".equals(segments[index])) {
        index++;

        if( index < segments.length && "devices".equals(segments[index])) {
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.PUT)) {
              editModbusDeviceToPlant_put_(context);
              return true;
            }  else             if ( context.isMethod(OpenAPIBase.Method.POST)) {
              addModbusDeviceToPlant_post_(context);
              return true;
            }  else             if ( context.isMethod(OpenAPIBase.Method.GET)) {
              getModbusDevices_get_(context);
              return true;
            } 
          } else           if ( index < segments.length ) {
            context.pathParameter("deviceId",segments[index]);
            index++;
            if ( segments.length == index) {
              if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
                removeModbusDevice_delete_(context);
                return true;
              } 
            }


          }          // end devices
        }  else         if( index < segments.length && "mappingFiles".equals(segments[index])) {
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.GET)) {
              getModbusMappingFiles_get_(context);
              return true;
            } 
          }

          // end mappingFiles
        } 

        // end modbus
      } 

      // end devicemanagement
    } 

    return false;
  }

private void addModbusDeviceToPlant_post_(OpenAPIContext context) throws Exception{

    context.setOperation("AddModbusDeviceToPlant");
ModbusDeviceData device_ = context.body(ModbusDeviceData.class);


    //  VALIDATORS 

    context.begin("AddModbusDeviceToPlant");
       if  ( context.require(device_, "device_") ) {
       device_.validate(context, "device_");
       }
    context.end();

    Object result = context.call( ()-> addModbusDeviceToPlant(device_));
    context.setResult(result, 200);

}

private void editModbusDeviceToPlant_put_(OpenAPIContext context) throws Exception{

    context.setOperation("EditModbusDeviceToPlant");
ModbusDeviceData device_ = context.body(ModbusDeviceData.class);


    //  VALIDATORS 

    context.begin("EditModbusDeviceToPlant");
       if  ( context.require(device_, "device_") ) {
       device_.validate(context, "device_");
       }
    context.end();

    Object result = context.call( ()-> editModbusDeviceToPlant(device_));
    context.setResult(result, 200);

}

private void getModbusDevices_get_(OpenAPIContext context) throws Exception{

    context.setOperation("GetModbusDevices");

    Object result = context.call( ()-> getModbusDevices());
    context.setResult(result, 200);

}

private void getModbusMappingFiles_get_(OpenAPIContext context) throws Exception{

    context.setOperation("GetModbusMappingFiles");

    Object result = context.call( ()-> getModbusMappingFiles());
    context.setResult(result, 200);

}

private void removeModbusDevice_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("RemoveModbusDevice");
String deviceId_ = context.toString(context.path("deviceId"));


    //  VALIDATORS 

    context.begin("RemoveModbusDevice");
    context.end();

    context.call( () -> { removeModbusDevice(deviceId_); return null; });
    context.setResult(null, 200);

}

}

