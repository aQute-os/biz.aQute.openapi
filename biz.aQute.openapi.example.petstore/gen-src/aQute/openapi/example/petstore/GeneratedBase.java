package aQute.openapi.example.petstore;

import java.time.LocalDate;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;

/**
 * <ul>
 * </ul>
 */

public abstract class GeneratedBase extends OpenAPIBase {

	public static final String				BASE_PATH		= "/v2";

	/*****************************************************************/

	public static OpenAPISecurityDefinition	petstore_auth	= OpenAPISecurityDefinition.implicit("petstore_auth",
			BASE_PATH, "http://petstore.swagger.io/api/oauth/dialog", null, "write:pets", "read:pets");

	public static OpenAPISecurityDefinition	api_key			= OpenAPISecurityDefinition.apiKey("api_key", BASE_PATH,
			"header", "api_key");

	public GeneratedBase() {
		super(BASE_PATH, aQute.openapi.example.petstore.GeneratedBase.class);
	}

	public static java.time.Instant toDateTime(String s) {
		return java.time.Instant.parse(s);
	}

	public static String fromDateTime(java.time.Instant s) {
		return s.toString();
	}

	public static LocalDate toDate(String s) {
		return LocalDate.parse(s);
	}

	public static String fromDate(LocalDate s) {
		return s.toString();
	}

	@Override
	public boolean dispatch_(OpenAPIContext context, String segments[], int index) throws Exception {

		return false;
	}

}
