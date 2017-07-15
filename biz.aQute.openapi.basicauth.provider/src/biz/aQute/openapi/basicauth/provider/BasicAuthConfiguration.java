package biz.aQute.openapi.basicauth.provider;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import biz.aQute.openapi.basicauth.provider.BasicAuthenticationProvider.Hash;

@ObjectClassDefinition(description = "OpenAPI Basic Authenticator for User Admin passwords")
@interface BasicAuthConfiguration {

	@AttributeDefinition(description = "Only valid when used with an encrypted line. Without encryption "
			+ "basic auth is extremely dangerous since passwords are send in the clear")
	boolean requireEncrypted() default true;


	@AttributeDefinition(description = "The realm to use when challenging the requester")
	String realm() default "default";

	@AttributeDefinition(description = "The key used in UserAdmin User properties for the basic auth id. If the key is empty the UserAdmin user name is used. The key must be a valid unescaped key in a filter.")
	String idkey() default "email";

	@AttributeDefinition(description = "The key used in UserAdmin User credentials for the basic auth (hashed) password.  The key must be a valid unescaped key in a filter.")
	String pwkey() default "password";

	@AttributeDefinition(description = "The security definition name used in the OpenAPI source.  The name must be a valid unescaped key in a filter.")
	String name() default "basicauth";

	@AttributeDefinition(description = "Hashing algorithm to use for the stored passwords. Use PLAIN only for debug never for production.")
	Hash hash() default Hash.SHA_256;

	@AttributeDefinition(description = "Salt bytes to use in the password hash. hashed = HASH(salt + plain). Salt is unique for each password")
	int salt() default 32;
}

