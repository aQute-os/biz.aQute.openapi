package aQute.openapi.example.petstore.user;

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
 * <li>{@link #createUsersWithArrayInput(List<User>) POST /user/createWithArray =  createUsersWithArrayInput}
 * 
 * <li>{@link #loginUser(String,String) GET /user/login =  loginUser}
 * 
 * <li>{@link #logoutUser() GET /user/logout =  logoutUser}
 * 
 * <li>{@link #deleteUser(String) DELETE /user/<b>[username]</b> =  deleteUser}
 * 
 * <li>{@link #getUserByName(String) GET /user/<b>[username]</b> =  getUserByName}
 * 
 * <li>{@link #updateUser(String,User) PUT /user/<b>[username]</b> =  updateUser}
 * 
 * <li>{@link #createUser(User) POST /user =  createUser}
 * 
 * <li>{@link #createUsersWithListInput(List<User>) POST /user/createWithList =  createUsersWithListInput}
 * 
 * </ul>
 * 
 */

@RequireGeneratedUser
public abstract class GeneratedUser extends OpenAPIBase {

public static final String BASE_PATH = "/v2";

/**
 * 
 * POST /user/createWithArray = createUsersWithArrayInput
 * 
 * Creates list of users with given input array
 * 
 * 
 * 
 * @param body – List of user object (body) collectionFormat=%scsv
 * 
   * @throws Response default / successful operation
 * default
 * 
 */

protected abstract void createUsersWithArrayInput(List<User> body) throws Exception;

/**
 * 
 * GET /user/login = loginUser
 * 
 * Logs user into the system
 * 
 * 
 * 
 * @param username – The user name for login (query) collectionFormat=%scsv
 * 
 * @param password – The password for login in clear text (query) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid username/password supplied
 * 200
 * 
 * X-Rate-Limit - int
 * 
 * X-Expires-After - java.time.Instant
 * 
 * 400
 * 
 */

protected abstract String loginUser(String username, String password) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /user/logout = logoutUser
 * 
 * Logs out current logged in user session
 * 
 * 
 * 
   * @throws Response default / successful operation
 * default
 * 
 */

protected abstract void logoutUser() throws Exception;

/**
 * 
 * DELETE /user/{username} = deleteUser
 * 
 * Delete user
 * 
 * This can only be done by the logged in user.
 * 
 * @param username – The name that needs to be deleted (path) collectionFormat=%scsv
 * 
   * @throws Response BadRequestResponse / Invalid username supplied
   * @throws Response 404 / User not found
 * 400
 * 
 * 404
 * 
 */

protected abstract void deleteUser(String username) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /user/{username} = getUserByName
 * 
 * Get user by user name
 * 
 * 
 * 
 * @param username – The name that needs to be fetched. Use user1 for testing.  (path) collectionFormat=%scsv
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid username supplied
   * @returns 404 / User not found
 * 200
 * 
 * 400
 * 
 * 404
 * 
 */

protected abstract User getUserByName(String username) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * PUT /user/{username} = updateUser
 * 
 * Updated user
 * 
 * This can only be done by the logged in user.
 * 
 * @param username – name that need to be deleted (path) collectionFormat=%scsv
 * 
 * @param body – Updated user object (body) collectionFormat=%scsv
 * 
   * @throws Response BadRequestResponse / Invalid user supplied
   * @throws Response 404 / User not found
 * 400
 * 
 * 404
 * 
 */

protected abstract void updateUser(String username, User body) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * POST /user = createUser
 * 
 * Create user
 * 
 * This can only be done by the logged in user.
 * 
 * @param body – Created user object (body) collectionFormat=%scsv
 * 
   * @throws Response default / successful operation
 * default
 * 
 */

protected abstract void createUser(User body) throws Exception;

/**
 * 
 * POST /user/createWithList = createUsersWithListInput
 * 
 * Creates list of users with given input array
 * 
 * 
 * 
 * @param body – List of user object (body) collectionFormat=%scsv
 * 
   * @throws Response default / successful operation
 * default
 * 
 */

protected abstract void createUsersWithListInput(List<User> body) throws Exception;

/**
 * 
 * User
 * 
 */

public static class User extends OpenAPIBase.DTO {

    public Optional<String> firstName = Optional.empty();
    public Optional<String> lastName = Optional.empty();
    public Optional<String> password = Optional.empty();
    public Optional<Integer> userStatus = Optional.empty();
    public Optional<String> phone = Optional.empty();
    public Optional<Long> id = Optional.empty();
    public Optional<String> email = Optional.empty();
    public Optional<String> username = Optional.empty();

    public User firstName(String firstName){ this.firstName=Optional.ofNullable(firstName); return this; }
    public Optional<String> firstName(){ return this.firstName; }

    public User lastName(String lastName){ this.lastName=Optional.ofNullable(lastName); return this; }
    public Optional<String> lastName(){ return this.lastName; }

    public User password(String password){ this.password=Optional.ofNullable(password); return this; }
    public Optional<String> password(){ return this.password; }

    public User userStatus(Integer userStatus){ this.userStatus=Optional.ofNullable(userStatus); return this; }
    public Optional<Integer> userStatus(){ return this.userStatus; }

    public User phone(String phone){ this.phone=Optional.ofNullable(phone); return this; }
    public Optional<String> phone(){ return this.phone; }

    public User id(Long id){ this.id=Optional.ofNullable(id); return this; }
    public Optional<Long> id(){ return this.id; }

    public User email(String email){ this.email=Optional.ofNullable(email); return this; }
    public Optional<String> email(){ return this.email; }

    public User username(String username){ this.username=Optional.ofNullable(username); return this; }
    public Optional<String> username(){ return this.username; }

}

  /*****************************************************************/

  public GeneratedUser() {
    super(BASE_PATH,aQute.openapi.example.petstore.GeneratedBase.class,
         "createUsersWithArrayInput POST   /user/createWithArray  PAYLOAD List<User>",
         "loginUser            GET    /user/login?username&password  RETURN String",
         "logoutUser           GET    /user/logout",
         "deleteUser           DELETE /user/{username}",
         "getUserByName        GET    /user/{username}  RETURN User",
         "updateUser           PUT    /user/{username}  PAYLOAD User",
         "createUser           POST   /user  PAYLOAD User",
         "createUsersWithListInput POST   /user/createWithList  PAYLOAD List<User>");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "user".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          createUser_post_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("POST");

      } else       if( index < segments.length && "createWithArray".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.POST)) {
            createUsersWithArrayInput_post_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("POST");

        }

        // end createWithArray
      }  else       if( index < segments.length && "createWithList".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.POST)) {
            createUsersWithListInput_post_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("POST");

        }

        // end createWithList
      }  else       if( index < segments.length && "login".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            loginUser_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("GET");

        }

        // end login
      }  else       if( index < segments.length && "logout".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            logoutUser_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("GET");

        }

        // end logout
      }  else         if ( index < segments.length ) {
        context.pathParameter("username",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
            deleteUser_delete_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            getUserByName_get_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.PUT)) {
            updateUser_put_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("DELETE", "GET", "PUT");

        }


      }

      // end user
    } 

    return false;
  }

private void createUsersWithArrayInput_post_(OpenAPIContext context) throws Exception{

    context.setOperation("createUsersWithArrayInput");
List<User> body_ = context.listBody(User.class);


    //  VALIDATORS 

    context.begin("createUsersWithArrayInput");
    context.end();

    context.call( () -> { createUsersWithArrayInput(body_); return null; });
    context.setResult(null, 200);

}

private void loginUser_get_(OpenAPIContext context) throws Exception{

    context.setOperation("loginUser");
String username_ = context.toString(context.parameter("username"));
String password_ = context.toString(context.parameter("password"));


    //  VALIDATORS 

    context.begin("loginUser");
    context.end();

    Object result = context.call( ()-> loginUser(username_, password_));
    context.setResult(result, 200);

}

private void logoutUser_get_(OpenAPIContext context) throws Exception{

    context.setOperation("logoutUser");

    context.call( () -> { logoutUser(); return null; });
    context.setResult(null, 200);

}

private void deleteUser_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("deleteUser");
String username_ = context.toString(context.path("username"));


    //  VALIDATORS 

    context.begin("deleteUser");
    context.end();

    context.call( () -> { deleteUser(username_); return null; });
    context.setResult(null, 200);

}

private void getUserByName_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getUserByName");
String username_ = context.toString(context.path("username"));


    //  VALIDATORS 

    context.begin("getUserByName");
    context.end();

    Object result = context.call( ()-> getUserByName(username_));
    context.setResult(result, 200);

}

private void updateUser_put_(OpenAPIContext context) throws Exception{

    context.setOperation("updateUser");
String username_ = context.toString(context.path("username"));
User body_ = context.body(User.class);


    //  VALIDATORS 

    context.begin("updateUser");
    context.end();

    context.call( () -> { updateUser(username_, body_); return null; });
    context.setResult(null, 200);

}

private void createUser_post_(OpenAPIContext context) throws Exception{

    context.setOperation("createUser");
User body_ = context.body(User.class);


    //  VALIDATORS 

    context.begin("createUser");
    context.end();

    context.call( () -> { createUser(body_); return null; });
    context.setResult(null, 200);

}

private void createUsersWithListInput_post_(OpenAPIContext context) throws Exception{

    context.setOperation("createUsersWithListInput");
List<User> body_ = context.listBody(User.class);


    //  VALIDATORS 

    context.begin("createUsersWithListInput");
    context.end();

    context.call( () -> { createUsersWithListInput(body_); return null; });
    context.setResult(null, 200);

}

}

