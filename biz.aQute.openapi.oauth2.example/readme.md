# OAuth2 Example

This example demonstrates the end-end use of the OpenAPI suite OAuth2 Authenticator. It shows how to create an application that is protected by a Google user id that is obtained via OpenId Connect.

## OAuth2

OAuth2 is a protocol to for third party authorization. The purpose is to authorize a _client_ application to act on behalf of a _user_ without providing that client application with actual credentials. The primary OAuth2 protocol uses browser redirection to authenticate the client on an authorization server and allows the user to select what _scopes_ the client is permitted to use. The authorization server then redirects the browser back to the client server where the client server or the client's code in the browser extracts a code. This code can then be exchanged for an opaque _access token_. This access token can be used by the client to then access resources on behalf of the user.

OAuth2 is a (third party) _authorization_ protocol, not an _authentication_ protocol. However, in the OpenAPI context the protocol is actually used to authenticate the user, which is clearly not part of the OAuth2 protocol. However, OpenId has created an extension that does authenticate the user based on OAuth2. Facebook, Github and others have APIs that provide user information. The OpenAPI suite OAuth2 authenticator supports OpenId connect and a number of extensions.

OAuth2 is a complex protocol that requires significant setup configuration. The following section will take you in detail how to setup Google as an OAuth2 provider.

## Getting A Client Id and Secret

OAuth2 requires a client id and a client secret to function. For this you need to register with the OAuth2 provider. In this example the provider is _Google_. You will have to log in to Google and go to the [credentials](https://console.developers.google.com/apis/credentials) page. This may require you to login.

![google credentials page](https://user-images.githubusercontent.com/200494/28531364-ecb2e8a0-7096-11e7-8966-ab3483890ef9.png)

The given page allows you to create new credentials. This will give you two hard to read strings:

* Client Id – The client id is the identifier for a request. The client id is public, you can encode it a Javascript application or HTML pages.
* Secret – The secret should not leave the server, never transmit it to the browser. On the server it should be regarded as confidential.

## OpenAPI Source

We will make a simple microservice that returns the authorization of a named action. The OpenAPI source looks as follows:

	{
	  "swagger":"2.0",
	  "securityDefinitions":{
	    "google":{
	      "type":"oauth2",
	      "flow":"accessCode",
	      "scopes":{ "openid":"", "email":"" },
	      "authorizationUrl":"",
	      "tokenUrl":""
	    }
	  },

The `securityDefinition` defines an `accessCode` flow. In this flow the server receives a code to get the actual access token from the token server. The name of the OAuth2 authenticator is `google`. Google is an OpenId implementer. For this reason, we specify the `openid` and `email` scopes required by OpenId Connect.

The `security` section in the prolog defines the default for all the operations. In this case, we want to make the `google` authenticator the default.

	  "security":[{
	      "google":[]
	  }],


The `authenticated` and `unauthenticated` operation takes an action and returns a boolean. The boolean reflects if the user has permission to an action or not. The `authenticated`  operation requires that the request is authenticated by Google and the  `unauthenticated` request disables the default and does not require any security. However, both methods check the permission of te action that is provided as a _path parameter_.

	  "basePath":"/openapi/security/google",
	  "paths":{
	    "/authenticated/{action}":{
	      "get":{
	        "operationId":"authenticated",
	        "parameters":[{
	            "name":"action",
	            "in":"path",
	            "required":true,
	            "type":"string"
	          }
	        ],
	        "responses":{
	          "200":{
	            "schema":{
	              "type":"boolean"
	            }
	          }
	        }
	      }
	    },
	    "/unauthenticated/{action}":{
	      "get":{
	        "operationId":"unauthenticated",
	        "security":[ ],
	        "parameters":[{
	            "name":"action",
	            "in":"path",
	            "required":true,
	            "type":"string"
	          }
	        ],
	        "responses":{
	          "200":{
	            "schema":{
	              "type":"boolean"
	            }
	          }
	        }
	      }
	    }
	  }
	}

## Java Code

The Java Code is a simple out of the box Web Application. The application class is also used as the microservice implementation class.

	@ProvideOAuth2Base
	@Component(name="biz.aQute.openapi.oauth2.example", service=OpenAPIBase.class)
	public class OAuth2Example extends OAuth2Base {
	  @Override
	  protected boolean authenticated(String action) throws Exception {
	    return hasPermission(action);
	  }
	  @Override
	  protected boolean unauthenticated(String action) throws Exception {
	    return hasPermission(action);
	  }
	}

 Don't let the annotations scare you too much, they're just there to ensure the proper libraries are dragged in.

## Javascript code

In the Basic Authentication case it was possible to get away with just command line access. However, OAuth2 requires that the browser is redirected.

