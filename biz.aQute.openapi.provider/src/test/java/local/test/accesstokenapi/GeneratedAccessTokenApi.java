package local.test.accesstokenapi;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;

public abstract class GeneratedAccessTokenApi extends OpenAPIBase {

  public final static String BASE_PATH = "/api/v1";

  /**
   * Generate an access token.
   * The endpoint to validate users credentials and generate an access token.
   * @param username The username. (in query)
   * @param password The password. (in query)
   * @return OK
   * @throws Response 0 / Unexpected error
   * 200
   * 0
   */
  abstract protected TokenResult accessTokenPost(String username, String password) throws Exception;

  /**
   * Refresh an access token.
   * The endpoint to refresh an access token.
   * @return OK
   * @throws Response 0 / Unexpected error
   * 200
   * 0
   */
  abstract protected TokenResult refresh() throws Exception;

  /**
   * @param id null (in path)
   * @param password null (in query)
   * @return OK
   * @throws Response 0 / Unexpected error
   * 200
   * 0
   */
  abstract protected TokenResult accessTokenGet(long id, char[] password) throws Exception;

  /**
   * TokenResult
   * 
   */

  public static class TokenResult extends OpenAPIBase.DTO {
    public long expireDateTime;
    public String accessToken;
  }

  /*****************************************************************/

  protected GeneratedAccessTokenApi() {
    super(BASE_PATH);
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

      if( "accessToken".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            accessTokenPost_get_(context);
            return true;
          } 
        } else         if( "refresh".equals(segments[index])) {
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.POST)) {
              refresh_post_(context);
              return true;
            } 
          }

          // end refresh
        }  else         {
          context.pathParameter("id",segments[index]);
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.GET)) {
              accessTokenGet_get_(context);
              return true;
            } 
          }


        }        // end accessToken
      } 

    return false;
  }

  private void accessTokenPost_get_(OpenAPIContext context) throws Exception {

    context.setOperation("AccessTokenPost");
    String username_ = context.toString(context.parameter("username"));
    String password_ = context.toString(context.parameter("password"));


    //  VALIDATORS 

    context.begin("AccessTokenPost");
    context.require(username_,"username");
    context.require(password_,"password");
    context.end();

    Object result = accessTokenPost(username_, password_);
    context.setResult(result, 200);
  }

  private void refresh_post_(OpenAPIContext context) throws Exception {

    context.setOperation("Refresh");

    Object result = refresh();
    context.setResult(result, 200);
  }

  private void accessTokenGet_get_(OpenAPIContext context) throws Exception {

    context.setOperation("AccessTokenGet");
    Long id_ = context.toLong(context.path("id"));
    char[] password_ = context.toPassword(context.parameter("password"));


    //  VALIDATORS 

    context.begin("AccessTokenGet");
    context.require(id_,"id");
    context.require(password_,"password");
    context.end();

    Object result = accessTokenGet(id_, password_);
    context.setResult(result, 200);
  }

}
