package aQute.openapi.oauth2.provider;

public enum ErrorEnum {
	invalid_request(" The request is missing a required parameter, includes an " +
			"unsupported parameter value (other than grant type), " +
			"repeats a parameter, includes multiple credentials, " +
			"utilizes more than one mechanism for authenticating the " +
			"client, or is otherwise malformed."), //
	invalid_client("Client authentication failed (e.g., unknown client, no\n" +
			"client authentication included, or unsupported\n" +
			"authentication method).  The authorization server MAY\n" +
			"return an HTTP 401 (Unauthorized) status code to indicate\n" +
			"which HTTP authentication schemes are supported.  If the\n" +
			"client attempted to authenticate via the \"Authorization\"\n" +
			"request header field, the authorization server MUST\n" +
			"respond with an HTTP 401 (Unauthorized) status code and\n" +
			"include the \"WWW-Authenticate\" response header field\n" +
			"matching the authentication scheme used by the client."), //
	invalid_grant(
			"The provided authorization grant (e.g., authorization\n" +
					"code, resource owner credentials) or refresh token is\n" +
					"invalid, expired, revoked, does not match the redirection\n" +
					"URI used in the authorization request, or was issued to\n" +
					"another client."), //

	unauthorized_client("The authenticated client is not authorized to use this\n" +
			"authorization grant type."), //
	unsupported_grant_type(
			"The authorization grant type is not supported by the\n" +
					"authorization server.\n" +
					""), //
	invalid_scope(
			"The requested scope is invalid, unknown, malformed, or\n" +
					"exceeds the scope granted by the resource owner.\n" +
					""), //
	x_unknown_request(
			"An invalid redirect was detected"), //
	x_no_callback_expected(
			"A valid callback occurred but it was not expected for this user"), //
	x_callback_expired("callback period expired"), //
	x_unknown_error("Received an unknown error from the authorization server"), //
	ok(
			"All went well"), x_authentication_failed("Failed to find the user among the users"), //
	x_github_get_email(
			"Failed to get user identity from Github"), //
	x_no_such_user(
			"No user found that has the given id as a property"), //
	x_id_received(
			"There was no id received from the authentication server"), //
	x_jwt_verification_failed(
			"Unexpected error during JWT verification"), //
	x_bad_access_token_request(
			"Getting the access token failed"), //
	x_already_logged_in(
			"Login was called but user was already logged in"), //
	x_not_logged_in("Not logged in");

	ErrorEnum(String s) {

	}

	public static ErrorEnum toEnum(String error) {
		try {
			return ErrorEnum.valueOf(error);
		} catch (Exception e) {
			// ignore
		}
		return x_unknown_error;
	}
}
