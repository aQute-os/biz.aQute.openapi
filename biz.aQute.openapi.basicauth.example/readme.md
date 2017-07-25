# Basic Auth Example

This example demonstrates the use of the [HTTP Basic Authentication][1] in the OpenAPI suite. It creates a REST endpoint at `/openapi/security/basic` with two operations:

* `/authenticated/{action}` – Verify if the action is authorized while requiring authentication. The return is a `boolean`.
* `/unauthenticated/{action}` –  Verify if the action is authorized while not requiring authentication. The return is a `boolean`.

## OpenAPI Source

The OpenAPI source is listed in this directory as [`openapi.json`](openapi.json). It is annotated inline with the text here.

	{
	  "swagger": "2.0",
	  "basePath": "/openapi/security/basic",

The `securityDefinition` defines the definitions of a security _scheme_ and names it. A scheme definition defines a, which is `basic`, `oauth2`, or `apikey`. It also defines the parameters of the scheme type. The name of the definition can then be used in `security` sections.

In this case we define a security scheme with the name `basicauth` of type `basic`. This the well supported, very popular, but not very secure [Basic Authentication][1] scheme.

	"securityDefinitions": {
	  "basic": {
	    "type": "basic"
	  }
	},

On the top level we can set the default security scheme. The default scheme is used when an operation has no `security` section.  Since we've defined the `basicauth` scheme we can now refer to it here. The value of the field is an empty array. These are the parameters to the scheme. However, Basic Authentication does not have any parameters.

	"security": [ {
	  "basic": []
	} ],

And then the operations. The `authenticated` operation has no `security` section so it uses the default security scheme set of type `basic` with the name `basicauth`.

	"paths": {
	  "/authenticated/{action}": {
	  "get": {
	    "operationId": "authenticated",
	    "parameters": [{
	      "name"          : "action",
	      "in"            : "path",
	      "required"      : true,
	      "type"          : "string"
	    }],
	    "responses": {
	      "200": {
	        "schema": {
	           "type": "boolean"
	        }
	      }
	    }
	  }
	},

The `unauthenticated` operation has an actual empty `security` (list with no elements) section so it has **no** authentication. That is, the OpenAPI runtime can call that method without requiring authentication. Such methods are necessary to login.

	  "/unauthenticated/{action}": {
	  "get": {
	    "operationId": "authenticated",
	    "parameters": [{
	      "name"          : "action",
	      "in"            : "path",
	      "required"      : true,
	      "type"          : "string"
	    }],
	    "responses": {
	      "200": {
	        "schema": {
	           "type": "boolean"
	        }
	      }
	    }
	  }
	}

## Java Source

After we run `../gradlew opeanpi` in this project directory we create the sources in the `gen-src` directory. The parameters for this generation are enumerated in the [`build.gradle`](build.gradle) file.

In the `src` directory we extend the `BasicAuthBase` class that was generated and implement the required methods:

	@ProvideBasicAuthBase
	@Component(service=OpenAPIBase.class)
	public class BasicAuthExample extends BasicAuthBase {
		@Override
		protected boolean authenticated(String action) throws Exception {
			System.out.println("Authenticated " + action);
			return false;
		}
		@Override
		protected void unauthenticated() throws Exception {
			System.out.println("Unauthenticated " + action);
			return false;
		}
	}

The `@ProvideBasicAuthBase` annotation is the counterpart of a requirement that each generated source has to ensure there is an implementation. The `@RequireConfigurerExtender` annotation ensures we include the [OSGi enRoute Configurer](https://github.com/osgi/osgi.enroute.bundles/tree/master/osgi.enroute.configurer.simple.provider), which we'll need to configure the runtime.

## The bnd File

The bundle we create, [`biz.aQute.openapi.basicauth.example`](biz.aQute.openapi.basicauth.example), contains the implementation as well as the generated code. The [bnd file](bnd.bnd) file looks as follows:

	Bundle-Description: \
		An OpenAPI example for using basic authentication and the \
		setting up the authorization.

	Private-Package: \
		aQute.openapi.basicauth.example

	-buildpath: \
		biz.aQute.openapi.provider, \
		osgi.enroute.base.api

	# needed to tell bnd that we have generated sources
	src = src, gen-src

## Configuring the Basic Authentication Provider

In this example we use the OSGi enRoute Configurer, we required this in the main class. We need the following configuration in the [`configuration/configuration.json`](configuration/configuration.json) file:

	[{
   	    "comment":             "Factory configuration for Basic Authentication",
        "service.factoryPid":  "biz.aQute.openapi.basicauth",
        "service.pid":         "1",
        "openapi.name":        "basic",
	    "requireEncrypted":    false,
	    "realm":               "Basic",
	    "idkey":               "email",
	    "pwkey":               "password",
	    "hash":                "SHA_256",
	    "salt":                32,
	    "reportingEndpoint":   "http://localhost:8080/close.html"
	}]


## Running the Code

The  [`basicauth.bndrun`](basicauth.bndrun) file specifies the run environment. This requires the following initial bundles:

* `biz.aQute.openapi.basicauth.example` – The example project
* `biz.aQute.openapi.basicauth.provider` – The OpenAPI suite basic authentication provider
* `biz.aQute.openapi.security.useradmin.provider` – OpenAPI security need a persistent storage to store credentials and other user information. This bundle provides an implementation of the OpenAPISecurityEnvironment service.
* `org.apache.felix.webconsole.plugins.useradmin)` – Allows us to use the Apache Felix Webconsole to edit and view the User Admin setup
* `org.apache.felix.gogo.command,osgi.enroute.gogo.shell.provider` – Gogo because all OpenAPI suite bundles have Gogo commands to manipulate the environment.

We then resolve and run debug the `basicauth.bndrun` file. This will start a web server and a gogo shell.

### Dispatchers

We can first check if the system has been started correctly by looking at the active dispatchers:

	G! dispatchers
	/openapi/security/basic
	G! dispatcher /openapi/security/basic
	dispatcher /openapi/security/basic
	Base Path        /openapi/security/basic
	  BasicAuthExample
	     authenticated        GET    /authenticated/{action}  RETURN boolean
	     unauthenticated      GET    /unauthenticated/{action}  RETURN boolean

We can now test the code by going to [http://localhost:8080/openapi/security/basic/unauthenticated/foo], assuming a default setup. The OpenAPI suite contains a simple REST Gogo command to make a rest call.

	G! rest http://localhost:8080/openapi/security/basic/unauthenticated/foo
	Unauthenticated: Has permission false                                     #1
	GET /openapi/security/basic/unauthenticated/foo
	Transfer-Encoding             : chunked
	                              : HTTP/1.1 200 OK
	Access-Control-Allow-Origin   : *
	Date                          : Mon, 24 Jul 2017 10:35:12 GMT
	Content-Type                  : application/json
	<<<<<<<<<<<<<<<<<
	false
	>>>>>>>>>>>>>>>>>>

## Secure Protocol

The result shows that we can access the `unauthenticated` operation without providing credentials. However, we do get permission. At `#1` we see the message from the method implemented in the `BasicAuthExample` class. The method returns `false` because we've not setup permissions yet.

If we try the same command for the `authenticated` operation we get a security error:

	G! rest http://localhost:8080/openapi/security/basic/authenticated/foo
	GET /openapi/security/basic/authenticated/foo
	                              : HTTP/1.1 403 Forbidden
	Content-Length                : 0
	Date                          : Mon, 24 Jul 2017 12:19:33 GMT
	No content Server returned HTTP response code: 403 for URL:
	http://localhost:8080/openapi/security/basic/authenticated/foo

At first sight, one would expect that we should have been asked to provide the credentials. However, this would require an `WWW-Authenticate` header in the response?

The reason we can find in the log:

	WARN	biz.aQute.openapi.ua.basic :: Attempt to authenticate with basic
	        auth over an unencrypted line. If this is necessary, configure
	        PID 'biz.aQute.openapi.ua.basic.requireEncrypted'

Basic Authentication sends out the password and user name in the plain. Without encryption, this information could trivially be picked up by anybody with a network sniffer. For this reason, the default of the Basic Authentication provider is to deny access when the transport protocol is not HTTPS. For testing (and only for testing) one can override this check. For this, change the line that sets this in the [`configuration/configuration.json`](configuration/configuration.json) to `false`:

	  "requireEncrypted":					false,

We can now try again:

	G! rest http://localhost:8080/openapi/security/basic/authenticated/foo
	GET /openapi/security/basic/authenticated/foo
	                              : HTTP/1.1 401 Please login
	Cache-Control                 : must-revalidate,no-cache,no-store
	WWW-Authenticate              : Basic realm="Basic"
	Content-Length                : 280
	Date                          : Mon, 24 Jul 2017 12:49:08 GMT
	Content-Type                  : text/html;charset=iso-8859-1
	No content Server returned HTTP response code: 401 for URL: http://localhost:8080/openapi/security/basic/authenticated/foo

The response is a challenge (see the `WWW-Authenticate` header) to provide basic authentication credentials. However, we've no user setup yet!

## Setting up a User

We need to setup a user in the system. However, the Basic Authentication provider has many options. The property key we use for the _name_ of the user, the _salt_ length, the _digest_ algorithm, etc. There are so many options that calculating the properties for a authenticating user with Basic Authentication is nearly impossible. Trying to just get the digesting right can be a major problem. Therefore, the Basic Authentication provider has a Gogo command (and a service API) to set the password for a user: `basic:passwd`. However, this command requires a system user id, so we first have to create a user. This can be done with the Gogo commands of the OpenAPI suite User Admin environment.

	G! useradd u123456
	User               u123456
	In groups          []
	Authorized         [u123456]
	Properties         {}
	Credentials        []
	G! passwd -p basic -i u123456 john.doe@example.com secret
	john.doe@example.com

About the unreadable user id. In general it is better to separate the id used for logging in from the user name. This is model is supported, it gives problems when users want to change their ids. A very good practice in our industry is to always make the actualy user name in User Admin an opaque identity that has no meaning. The properties can then be used to set the user's profile information. In this case we use the email address of the user for the login id but have given the user a unique number as name in User Admin. It also allows you to use different and multiple ids. For example, API keys can easily be implemented this way.

Also, and for some more important, users and groups share the same name namespace. For this reason a user should not be capable of picking a name that could be confused with a group. Especially since group names are used for authorization checks as we will see later. Ergo, don't let the user pick the name for the User Admin user name.

The Basic Authentication `passwd` command takes the following parameters:

* `-p`, `--provider` – The name of the provider. You can find the providers with the `providers` command.
* `-i`, `--identity` – The identity of the user. Generally the credentials do not use the actual user identity but a property of this identity, for example, the email address.
* user name – The name used for the user.
* password – The secret to use.

	G! passwd -p basic -i u123456 john.doe@example.com secret
	john.doe@example.com


## Authenticating

We can now access the authenticated operation:

	$ rest --user john.doe@example.com:secret http://localhost:8080/openapi/security/basic/authenticated/foo
	Authenticated foo
	GET /openapi/security/basic/authenticated/foo
	Transfer-Encoding             : chunked
	                              : HTTP/1.1 200 OK
	Access-Control-Allow-Origin   : *
	Date                          : Mon, 24 Jul 2017 12:54:07 GMT
	Content-Type                  : application/json
	<<<<<<<<<<<<<<<<<
	false
	>>>>>>>>>>>>>>>>>>

This worked. Clearly we're not authorized yet but we could execute the `authenticated` operation.

## Permissions

The next step is to _authorize_ the request. The OpenAPI suite uses the [OSGi enRoute Authority][2] to authorize requests. In this example we use an Authority implementation that is fully based on User Admin environment. The `Authority` interface's methods `hasPermission` and `checkPermission` are also added to the `OpenAPIBase` class so implementation code can directly call `hasPermission` and `getPermission` on `this`.

A permission is in the end a _structured string_. (The syntax is the same as Apache Shiro.) It basically consists of _parts_ separated with colons (':'). A part can be a wildcard, a literal match or a number of alternative literal matches. For example:

	read:device:n5629238
	read:user:basic,friends,password:u123456
	read:user:*:*

## Authorizing a User

To authorize John Doe (our current user) we need to set the permissions of the `u123456` user. In User Admin there is the concept of the `Authorization` object. You can get this object for each user. It is associated with all _implied_ roles of the user. The meaning of _implies_ can be quite simple and rather complex. In the simple form, groups hold _basic members_. A group implies its basic members and their basic members (for the groups in there) recursively. Group names are the operators here. A group name is the earlier defined _permission_. Each permission has therefore a set of users or implied permissions. Confused? Let's try a few examples.

So far we tested if the `foo` action was authorized. To create a `foo` permission we can create a group with the name `foo`. The Gogo command `usermod` makes that quite easy to do:

	G! usermod u123456 foo
	usermod u123456 foo
	User               u123456
	In groups          [foo]
	Authorized         [u123456, foo]
	Properties         {email=john.doe@example.com}
	Credentials        [password, password.salt]
	G!

If we now attempt to access the URL we see that we do have permission for `foo`.

	G! rest --user john.doe@example.com:secret http://localhost:8080/openapi/security/basic/authenticated/foo
	Authenticated foo
	GET /openapi/security/basic/authenticated/foo
	Transfer-Encoding             : chunked
	                              : HTTP/1.1 200 OK
	Access-Control-Allow-Origin   : *
	Date                          : Mon, 24 Jul 2017 13:01:42 GMT
	Content-Type                  : application/json
	<<<<<<<<<<<<<<<<<
	true
	>>>>>>>>>>>>>>>>>>

## More Complicate Permissions

To make it slightly more complicated we could add an action `view:a`, `view:b` and `view:c`. We then give John Doe permission to see `view:a,c`.

	G! usermod u123456 view:a,c
	User               u123456
	In groups          [foo, view:a,c]
	Authorized         [u123456, foo, view:a,c]
	Properties         {aQute.ua.id=john.doe@example.com}
	Credentials        [aQute.ua.pw, aQute.ua.pw.salt]

You can now try out the following URLs:

	 [http://localhost:8080/openapi/security/basic/authenticated/view:a] true
	 [http://localhost:8080/openapi/security/basic/authenticated/view:b] false
	 [http://localhost:8080/openapi/security/basic/authenticated/view:c] true


## Roles

In the previous example we directly added the user to the permission groups. Clearly, that does not scale well. Normally, the idea is to use _roles_ and assign a role to a user. The actual permissions are then associated with a role. For example, assume we have the following roles:

* Admin
* User
* Installer

Assume also we have the following permission grammar:

	permission ::=  verb ':' type ':' instance
	verb	   ::= 'create' | 'read' | 'update' | 'delete'
	type       ::= 'user' | 'device' | 'route' | 'channel'
	instance   ::= <some name>

We first give the Admin role permission to do anything:

	G! groupadd Admin *:*:*
	Group              Admin
	In groups          [*:*:*]
	Basic Members      []
	Requried Members   []
	Properties         {}
	Credentials        []

We then give the user the permission to read all state except for the `user` types:

	G! groupadd User read:device,route,channel:*

The `Installer` role can create update everything except the `user` type.

	G! groupadd Installer *:device,route,channel:*

Lets give John Doe the User role. We can use the `-r` option to not just add the given group but replace all groups for that user:

	G! usermod -r u123456 User

We can now test the roles directly from the shell:

	G! implies u123456 read:device:d54123
	true
	G! implies u123456 create:device:d54123
	false

So lets see what happens when John Doe is promoted to an installer:

	G! usermod --replace u123456 Installer
	User               u123456
	In groups          [Installer]
	Authorized         [*:device,route,channel:*, Installer, u123456]
	Properties         {aQute.ua.id=john.doe@example.com}
	Credentials        [aQute.ua.pw, aQute.ua.pw.salt]
	G! implies u123456 create:device:45678
	true
	G! implies u123456 create:user:u653229
	false

Clearly when we make John an Admin then he has no more restrictions:

	G! usermod --replace u123456 Admin
	User               u123456
	In groups          [Admin]
	Authorized         [*:*:*, u123456, Admin]
	Properties         {aQute.ua.id=john.doe@example.com}
	Credentials        [aQute.ua.pw, aQute.ua.pw.salt]
	G! implies u123456 create:user:u653229
	true
	G! implies u123456 what:ever:isneeded
	true

In this model we do not allow a User role to see any user information. However, often you need to be able to see and update your own information. This can be managed with the following:

	G! usermod -r u123456 User read,update:user:u123456
	...
	G! implies u123456 read:user:u123456
	true
	G! implies u123456 read:user:uXXXXXX
	false


## Advanced

Constructing this string with dynamic arguments requires escaping. To make this easier, the `hasPermission` and `checkPermission` calls take  _varargs_ for the dynamic parts.

	hasPermission( "read:user", user.getName() );

This will automatically escape any characters that are part of the syntax. Since the comma (','), colon (':'), and wildcard ('*') characters have special meaning, they need to be escaped with a backslash in a part when they should be literally matched.

The grammar of the permission string is:

	permission ::= part ( ':' part )*
	part       ::= '*' | literal ( ',' literal )*
	literal    ::= ( unescaped | escaped ) +
	escaped    ::= '\' [:*,\]
	unescaped  ::= [^:*,\]



[1]: https://tools.ietf.org/html/rfc7617
[2]: https://github.com/osgi/osgi.enroute/blob/master/osgi.enroute.base.api/src/osgi/enroute/authorization/api/Authority.java