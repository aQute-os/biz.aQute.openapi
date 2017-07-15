package aQute.openapi.oauth2.provider;

import java.net.URISyntaxException;
import java.util.Optional;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;

import aQute.openapi.user.api.OpenAPISecurity;

public class OpenIdHandler extends Handler {

	OpenIdHandler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws URISyntaxException {
		super(logger, config, def);
	}

	@Override
	public AuthenticateResult authenticate(AccessTokenResponse accessToken, OpenAPISecurity security) throws Exception {
		AuthenticateResult result = new AuthenticateResult();
		try {
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setSkipSignatureVerification()
					.setSkipDefaultAudienceValidation()
					.build();

			JwtClaims claims = jwtConsumer.processToClaims(accessToken.id_token);
			String email = claims.getClaimValue("email", String.class);

			if (email != null) {
				Optional<String> user = security.getUser(idKey, email.toLowerCase());
				if (user.isPresent()) {
					result.user = user.get();
					return result;
				} else {
					result.error = ErrorEnum.x_no_such_user.toString();
					result.error_description = idKey + "=" + email;
					return result;
				}
			} else {
				result.error = ErrorEnum.x_id_received.toString();
				result.error_description = idKey + "=" + email;
				return result;
			}
		} catch (Exception e) {
			result.error = ErrorEnum.x_jwt_verification_failed.toString();
			return result;
		}
	}
}
