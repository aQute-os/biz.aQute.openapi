package aQute.openapi.example.petstore.pet;

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
 * <li>{@link #uploadFile(long,Optional<String>,Optional<OpenAPIBase.Part>) POST /pet/<b>[petId]</b>/uploadImage =  uploadFile}
 * 
 * <li>{@link #findPetsByTags(List<String>) GET /pet/findByTags =  findPetsByTags}
 * 
 * <li>{@link #updatePet(Pet) PUT /pet =  updatePet}
 * 
 * <li>{@link #getPetById(long) GET /pet/<b>[petId]</b> =  getPetById}
 * 
 * <li>{@link #deletePet(Optional<String>,long) DELETE /pet/<b>[petId]</b> =  deletePet}
 * 
 * <li>{@link #addPet(Pet) POST /pet =  addPet}
 * 
 * <li>{@link #findPetsByStatus(List<FindPetsByStatus_status>) GET /pet/findByStatus =  findPetsByStatus}
 * 
 * <li>{@link #updatePetWithForm(long,Optional<String>,Optional<String>) POST /pet/<b>[petId]</b> =  updatePetWithForm}
 * 
 * </ul>
 * 
 */

@RequireGeneratedPet
public abstract class GeneratedPet extends OpenAPIBase {

public static final String BASE_PATH = "/v2";

/**
 * 
 * POST /pet/{petId}/uploadImage = uploadFile
 * 
 * uploads an image
 * 
 * 
 * 
 * @param petId – ID of pet to update (path) collectionFormat=%scsv
 * 
 * @param additionalMetadata – Additional data to pass to server (formData) collectionFormat=%scsv
 * 
 * @param file – file to upload (formData) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
 * 200
 * 
 */

protected abstract ApiResponse uploadFile(long petId, Optional<String> additionalMetadata, Optional<OpenAPIBase.Part> file) throws Exception;

/**
 * 
 * GET /pet/findByTags = findPetsByTags
 * 
 * Finds Pets by tags
 * 
 * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
 * 
 * @param tags – Tags to filter by (query) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid tag value
 * 200
 * 
 * 400
 * 
 */

protected abstract Iterable<? extends Pet> findPetsByTags(List<String> tags) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * PUT /pet = updatePet
 * 
 * Update an existing pet
 * 
 * 
 * 
 * @param body – Pet object that needs to be added to the store (body) collectionFormat=%scsv
 * 
   * @throws Response BadRequestResponse / Invalid ID supplied
   * @throws Response 404 / Pet not found
   * @throws Response 405 / Validation exception
 * 400
 * 
 * 404
 * 
 * 405
 * 
 */

protected abstract void updatePet(Pet body) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /pet/{petId} = getPetById
 * 
 * Find pet by ID
 * 
 * Returns a single pet
 * 
 * @param petId – ID of pet to return (path) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid ID supplied
   * @returns 404 / Pet not found
 * 200
 * 
 * 400
 * 
 * 404
 * 
 */

protected abstract Pet getPetById(long petId) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * DELETE /pet/{petId} = deletePet
 * 
 * Deletes a pet
 * 
 * 
 * 
 * @param api_key –  (header) collectionFormat=%scsv
 * 
 * @param petId – Pet id to delete (path) collectionFormat=%scsv
 * 
   * @throws Response BadRequestResponse / Invalid pet value
 * 400
 * 
 */

protected abstract void deletePet(Optional<String> api_key, long petId) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * POST /pet = addPet
 * 
 * Add a new pet to the store
 * 
 * 
 * 
 * @param body – Pet object that needs to be added to the store (body) collectionFormat=%scsv
 * 
   * @throws Response 405 / Invalid input
 * 405
 * 
 */

protected abstract void addPet(Pet body) throws Exception;

/**
 * 
 * GET /pet/findByStatus = findPetsByStatus
 * 
 * Finds Pets by status
 * 
 * Multiple status values can be provided with comma separated strings
 * 
 * @param status – Status values that need to be considered for filter (query) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid status value
 * 200
 * 
 * 400
 * 
 */

protected abstract Iterable<? extends Pet> findPetsByStatus(List<FindPetsByStatus_status> status) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * POST /pet/{petId} = updatePetWithForm
 * 
 * Updates a pet in the store with form data
 * 
 * 
 * 
 * @param petId – ID of pet that needs to be updated (path) collectionFormat=%scsv
 * 
 * @param name – Updated name of the pet (formData) collectionFormat=%scsv
 * 
 * @param status – Updated status of the pet (formData) collectionFormat=%scsv
 * 
   * @throws Response 405 / Invalid input
 * 405
 * 
 */

protected abstract void updatePetWithForm(long petId, Optional<String> name, Optional<String> status) throws Exception;

/**
 * 
 * StatusEnum
 * 
 * pet status in the store
 * 
 */

  public enum StatusEnum {
    available("available"),
    pending("pending"),
    sold("sold");

    public final String value;

    StatusEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * Category
 * 
 */

public static class Category extends OpenAPIBase.DTO {

    public Optional<String> name = Optional.empty();
    public Optional<Long> id = Optional.empty();

    public Category name(String name){ this.name=Optional.ofNullable(name); return this; }
    public Optional<String> name(){ return this.name; }

    public Category id(Long id){ this.id=Optional.ofNullable(id); return this; }
    public Optional<Long> id(){ return this.id; }

}

/**
 * 
 * ApiResponse
 * 
 */

public static class ApiResponse extends OpenAPIBase.DTO {

    public Optional<Integer> code = Optional.empty();
    public Optional<String> type = Optional.empty();
    public Optional<String> message = Optional.empty();

    public ApiResponse code(Integer code){ this.code=Optional.ofNullable(code); return this; }
    public Optional<Integer> code(){ return this.code; }

    public ApiResponse type(String type){ this.type=Optional.ofNullable(type); return this; }
    public Optional<String> type(){ return this.type; }

    public ApiResponse message(String message){ this.message=Optional.ofNullable(message); return this; }
    public Optional<String> message(){ return this.message; }

}

/**
 * 
 * FindPetsByStatus_status
 * 
 */

  public enum FindPetsByStatus_status {
    available("available"),
    pending("pending"),
    sold("sold");

    public final String value;

    FindPetsByStatus_status(String value) {
      this.value = value;
    }
  }

/**
 * 
 * Tag
 * 
 */

public static class Tag extends OpenAPIBase.DTO {

    public Optional<String> name = Optional.empty();
    public Optional<Long> id = Optional.empty();

    public Tag name(String name){ this.name=Optional.ofNullable(name); return this; }
    public Optional<String> name(){ return this.name; }

    public Tag id(Long id){ this.id=Optional.ofNullable(id); return this; }
    public Optional<Long> id(){ return this.id; }

}

/**
 * 
 * Pet
 * 
 */

public static class Pet extends OpenAPIBase.DTO {

    public List<String> photoUrls;
    public String name;
    public Optional<Long> id = Optional.empty();
    public Optional<Category> category = Optional.empty();
    public Optional<List<Tag>> tags = Optional.empty();
    public Optional<StatusEnum> status = Optional.empty();

    public Pet photoUrls(List<String> photoUrls){ this.photoUrls=photoUrls; return this; }
    public List<String> photoUrls(){ return this.photoUrls; }

    public Pet name(String name){ this.name=name; return this; }
    public String name(){ return this.name; }

    public Pet id(Long id){ this.id=Optional.ofNullable(id); return this; }
    public Optional<Long> id(){ return this.id; }

    public Pet category(Category category){ this.category=Optional.ofNullable(category); return this; }
    public Optional<Category> category(){ return this.category; }

    public Pet tags(List<Tag> tags){ this.tags=Optional.ofNullable(tags); return this; }
    public Optional<List<Tag>> tags(){ return this.tags; }

    public Pet status(StatusEnum status){ this.status=Optional.ofNullable(status); return this; }
    public Optional<StatusEnum> status(){ return this.status; }

}

  /*****************************************************************/

  public GeneratedPet() {
    super(BASE_PATH,aQute.openapi.example.petstore.GeneratedBase.class,
         "uploadFile           POST   /pet/{petId}/uploadImage  RETURN ApiResponse",
         "findPetsByTags       GET    /pet/findByTags?tags  RETURN List<Pet>",
         "updatePet            PUT    /pet  PAYLOAD Pet",
         "getPetById           GET    /pet/{petId}  RETURN Pet",
         "deletePet            DELETE /pet/{petId}",
         "addPet               POST   /pet  PAYLOAD Pet",
         "findPetsByStatus     GET    /pet/findByStatus?status  RETURN List<Pet>",
         "updatePetWithForm    POST   /pet/{petId}");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "pet".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          updatePet_put_(context);
          return true;
        }  else         if ( context.isMethod(OpenAPIBase.Method.POST)) {
          addPet_post_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("PUT", "POST");

      } else       if( index < segments.length && "findByStatus".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            findPetsByStatus_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("GET");

        }

        // end findByStatus
      }  else       if( index < segments.length && "findByTags".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            findPetsByTags_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("GET");

        }

        // end findByTags
      }  else       if ( index < segments.length ) {
        context.pathParameter("petId",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
            deletePet_delete_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.POST)) {
            updatePetWithForm_post_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            getPetById_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("DELETE", "POST", "GET");

        } else         if( index < segments.length && "uploadImage".equals(segments[index])) {
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.POST)) {
              uploadFile_post_(context);
              return true;
            } 
            return getOpenAPIContext().doOptions("POST");

          }

          // end uploadImage
        } 


      }      // end pet
    } 

    return false;
  }

private void uploadFile_post_(OpenAPIContext context) throws Exception{

    context.setOperation("uploadFile");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
Long petId_ = context.toLong(context.path("petId"));
Optional<String> additionalMetadata_ = context.optional(context.toString(context.formData("additionalMetadata")));
Optional<OpenAPIBase.Part> file_ = context.optional(context.part("file"));


    //  VALIDATORS 

    context.begin("uploadFile");
    context.end();

    Object result = context.call( ()-> uploadFile(petId_, additionalMetadata_, file_));
    context.setResult(result, 200);

}

private void findPetsByTags_get_(OpenAPIContext context) throws Exception{

    context.setOperation("findPetsByTags");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
List<String> tags_ = context.toArray(String.class, context.csv(context.parameters("tags")));


    //  VALIDATORS 

    context.begin("findPetsByTags");
    context.end();

    Object result = context.call( ()-> findPetsByTags(tags_));
    context.setResult(result, 200);

}

private void updatePet_put_(OpenAPIContext context) throws Exception{

    context.setOperation("updatePet");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
Pet body_ = context.body(Pet.class);


    //  VALIDATORS 

    context.begin("updatePet");
    context.end();

    context.call( () -> { updatePet(body_); return null; });
    context.setResult(null, 200);

}

private void getPetById_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getPetById");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.api_key).verify();
Long petId_ = context.toLong(context.path("petId"));


    //  VALIDATORS 

    context.begin("getPetById");
    context.end();

    Object result = context.call( ()-> getPetById(petId_));
    context.setResult(result, 200);

}

private void deletePet_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("deletePet");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
Optional<String> api_key_ = context.optional(context.toString(context.header("api_key")));
Long petId_ = context.toLong(context.path("petId"));


    //  VALIDATORS 

    context.begin("deletePet");
    context.end();

    context.call( () -> { deletePet(api_key_, petId_); return null; });
    context.setResult(null, 200);

}

private void addPet_post_(OpenAPIContext context) throws Exception{

    context.setOperation("addPet");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
Pet body_ = context.body(Pet.class);


    //  VALIDATORS 

    context.begin("addPet");
    context.end();

    context.call( () -> { addPet(body_); return null; });
    context.setResult(null, 200);

}

private void findPetsByStatus_get_(OpenAPIContext context) throws Exception{

    context.setOperation("findPetsByStatus");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
List<FindPetsByStatus_status> status_ = context.toArray(FindPetsByStatus_status.class, context.csv(context.parameters("status")));


    //  VALIDATORS 

    context.begin("findPetsByStatus");
       if  ( context.require(status_, "status_") ) {
    context.validate(status_.size() <= 10, status_, "status_", "status_.size() <= 10");
    context.validate(status_.size() >= 0, status_, "status_", "status_.size() >= 0");
       }
    context.end();

    Object result = context.call( ()-> findPetsByStatus(status_));
    context.setResult(result, 200);

}

private void updatePetWithForm_post_(OpenAPIContext context) throws Exception{

    context.setOperation("updatePetWithForm");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.petstore_auth,"write:pets","read:pets").verify();
Long petId_ = context.toLong(context.path("petId"));
Optional<String> name_ = context.optional(context.toString(context.formData("name")));
Optional<String> status_ = context.optional(context.toString(context.formData("status")));


    //  VALIDATORS 

    context.begin("updatePetWithForm");
    context.end();

    context.call( () -> { updatePetWithForm(petId_, name_, status_); return null; });
    context.setResult(null, 200);

}

}

