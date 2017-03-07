package local.test.accesstokenapi;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;

/**
 * <ul>
 * <li>{@link #accessTokenPost(String,String) POST /accessToken =
 * AccessTokenPost}
 * <li>{@link #refresh() POST /accessToken/refresh = Refresh}
 * </ul>
 */

@RequireGeneratedAccessTokenApi
public abstract class GeneratedAccessTokenApi extends OpenAPIBase {

	public static final String BASE_PATH = "/api/v1";

	/**
	 * POST /accessToken = AccessTokenPost Generate an access token. The
	 * endpoint to validate users credentials and generate an access token.
	 * 
	 * @param username – The username. (query)
	 * @param password – The password. (query)
	 * @returns 200 / OK
	 * @returns 0 / Unexpected error 200 0
	 */

	protected abstract TokenResult accessTokenPost(String username, String password) throws Exception;

	/**
	 * POST /accessToken/refresh = Refresh Refresh an access token. The endpoint
	 * to refresh an access token.
	 * 
	 * @returns 200 / OK
	 * @returns 0 / Unexpected error 200 0
	 */

	protected abstract TokenResult refresh() throws Exception;

	/**
	 * TokenResult
	 */

	public static class TokenResult extends OpenAPIBase.DTO {

		public java.time.OffsetDateTime	expireDateTime;
		public String					accessToken;

		public TokenResult expireDateTime(java.time.OffsetDateTime expireDateTime) {
			this.expireDateTime = expireDateTime;
			return this;
		}

		public java.time.OffsetDateTime expireDateTime() {
			return this.expireDateTime;
		}

		public TokenResult accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public String accessToken() {
			return this.accessToken;
		}

	}

	/*****************************************************************/

	public GeneratedAccessTokenApi() {
		super(BASE_PATH, "AccessTokenPost      POST   /accessToken?username&password RETURN TokenResult",
				"Refresh              POST   /accessToken/refresh RETURN TokenResult");
	}

	public boolean dispatch_(OpenAPIContext context, String segments[], int index) throws Exception {

		if (index < segments.length && "accessToken".equals(segments[index])) {
			index++;
			if (segments.length == index) {
				if (context.isMethod(OpenAPIBase.Method.POST)) {
					accessTokenPost_post_(context);
					return true;
				}
			} else if (index < segments.length && "refresh".equals(segments[index])) {
				index++;
				if (segments.length == index) {
					if (context.isMethod(OpenAPIBase.Method.POST)) {
						refresh_post_(context);
						return true;
					}
				}

				// end refresh
			}

			// end accessToken
		}

		return false;
	}

	private void accessTokenPost_post_(OpenAPIContext context) throws Exception {

		context.setOperation("AccessTokenPost");
		String username_ = context.toString(context.parameter("username"));
		String password_ = context.toString(context.parameter("password"));

		// VALIDATORS

		context.begin("AccessTokenPost");
		context.require(username_, "username");
		context.require(password_, "password");
		context.end();

		Object result = accessTokenPost(username_, password_);
		context.setResult(result, 200);

	}

	private void refresh_post_(OpenAPIContext context) throws Exception {

		context.setOperation("Refresh");

		Object result = refresh();
		context.setResult(result, 200);

	}

}

// aQute OpenAPI generator version 0
