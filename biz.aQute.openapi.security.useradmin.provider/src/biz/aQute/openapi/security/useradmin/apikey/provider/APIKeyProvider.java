package biz.aQute.openapi.security.useradmin.apikey.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;
import biz.aQute.openapi.security.useradmin.apikey.provider.APIKeyProvider.Config;

@Designate(ocd = Config.class, factory = true)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class APIKeyProvider implements OpenAPISecurityProvider {
	private static final String	SECRET_KEY			= "aQute.openapi.useradmin.secret";
	private static final String	ID_KEY				= "aQute.openapi.useradmin.id";

	static Pattern				API_KEY_FORMAT_P	= Pattern
			.compile("\\s*(?<id>[0-9a-z]){20,20}:(?<digest>[0-9a-z]){64}\\s*");

	@Reference
	UserAdmin					userAdmin;

	Config						config;
	final SecureRandom			random				= new SecureRandom();

	String						idkey;
	String						clientidKey;
	String						secretKey;

	@ObjectClassDefinition
	@interface Config {
		String name();

		String type();

		long maxDeltaInMs() default 120000;

		int bytes() default 32;

		String idkey() default "aQute.ua.id";

		String clientidkey() default "aQute.ua.clientid.${name}";

		String secretkey() default "aQute.ua.secret.${name}";
	}

	@Activate
	void activate(Config config) {
		this.config = config;
		this.clientidKey = config.clientidkey().replaceAll("\\$\\{name\\}", config.name());
		this.secretKey = config.secretkey().replaceAll("\\$\\{name\\}", config.name());
		this.idkey = config.idkey();
	}

	@Override
	public Authentication authenticate(OpenAPIContext context, OpenAPISecurityDefinition dto) {
		String value = getInValue(context, dto);
		Matcher matcher = API_KEY_FORMAT_P.matcher(value);

		return new Authentication() {

			private User user;

			@Override
			public void requestCredentials() throws IOException {
				throw new OpenAPIBase.ForbiddenResponse("No API Key found in request for " + dto.in + " " + dto.name);
			}

			@Override
			public boolean needsCredentials() {
				return value == null;
			}

			@Override
			public boolean isAuthenticated() throws NoSuchAlgorithmException {

				if (!matcher.matches()) {
					// log?
					return false;
				}

				String id = matcher.group("id");
				user = userAdmin.getUser(ID_KEY, id);
				if (user == null)
					return false;

				String secretString = (String) user.getCredentials().get(SECRET_KEY);
				if (secretString == null)
					return false;

				String date = context.header("date");
				if (date == null) {
					return false;
				}

				long sent = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(date)).toEpochMilli();
				long now = Instant.now().toEpochMilli();

				boolean preventReplayAttack = Math.abs(now - sent) < config.maxDeltaInMs();
				if (preventReplayAttack) {
					return false;
				}

				MessageDigest md = MessageDigest.getInstance("SHA-256");

				StringBuilder sb = new StringBuilder();
				sb.append(secretString).append("\n");
				sb.append(context.method()).append("\n");
				sb.append(context.path()).append("\n");
				sb.append(context.header("date")).append("\n");

				byte[] request = sb.toString().toLowerCase().getBytes(StandardCharsets.UTF_8);
				md.update(request);
				String actualDigest = DatatypeConverter.printHexBinary(md.digest());
				String expectedDigest = matcher.group("digest");
				boolean correctDigest = actualDigest.equalsIgnoreCase(expectedDigest);
				return correctDigest;
			}

			@Override
			public boolean ignore() {
				return false;
			}

			@Override
			public String getUser() {
				return user == null ? User.USER_ANYONE : user.getName();
			}
		};
	}

	protected String getInValue(OpenAPIContext context, OpenAPISecurityDefinition dto) {
		String value;
		switch (dto.in) {
		case "header":
			value = context.header(dto.name);
			if (value == null)
				throw new SecurityException("Required header missing " + dto.name);
			break;

		case "query":
			value = context.parameter(dto.name);
			if (value == null)
				throw new SecurityException("Required parameter missing: " + dto.name);
			break;

		default:
			throw new SecurityException("Invalid security provider specification, invalid 'in' for " + dto);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public String addAPIKey(String userId) {
		User user = getUser(userId);
		if (user == null)
			return "No such user " + idkey + "=" + userId;

		byte[] secret = new byte[config.bytes()];
		random.nextBytes(secret);

		byte[] id = new byte[config.bytes()];
		random.nextBytes(id);

		user.getProperties().put(clientidKey, Base64.getEncoder().encodeToString(id));
		user.getCredentials().put(secretKey, secret);

		return "key=" + id + " secret=" + Base64.getEncoder().encodeToString(secret);
	}

	public String removeAPIKey(String userId) {
		User user = getUser(userId);
		if (user == null)
			return "No such user " + idkey + "=" + userId;

		Object old = user.getProperties().remove(clientidKey);
		user.getCredentials().remove(secretKey);
		return old == null ? "No such key" : "" + old;
	}

	User getUser(String userId) {
		return idkey.isEmpty() ? (User) userAdmin.getRole(userId) : userAdmin.getUser(idkey, userId);
	}

	@Override
	public String toString() {
		return "ApiKeyImpl["+config.name()+"]";
	}

}
