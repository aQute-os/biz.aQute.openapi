# OAuth2 Authenticator

This project is part of the [OpenAPI Suite][openapi] and provides an _authenticator_ for the [OAuth2][oauth2] authorization protocol. The architecture for authenticators is explained in the [security section][security].

## Operation

OAuth2 is an authorization protocol has a number of actors:

* User (represented by the browser in the protocol)
* Client (the microservice) server
* Authorization server
* Token server
* Resource

The protocol was designed to allow the _client_ to access _resources_ on behalf of the _user_ without having any knowledge about the user itself. For this, the client redirects the user (the browser) to an _authorization server_. Since this is another website it can communicate privately with the user. This communication is used to authorize the client. When the user approves, the authentication server sends a _code_ to the client via another browser redirect. The authorization server adds the code to the URI of this redirect. The client server then extracts this code and uses it to get an _access token_ from the _token server_.

Since OAuth2 goes out of its way to hide the credentials and identity of the user, extensions are needed to authenticate the user. The [OpenId Connect][connect] specification provides framework based on OAuth2 to piggyback authentication information when getting the access token. OpenId Connect is supported.

When the authorization/token server does not support OpenId Connect, it is necessary to perform a call to a _resource_ that can provide this information. The token provided by the token server is then used for authorizing this authentication request.

This OAuth2 authenticator provides handlers for Github and Google authentication.

## The OAuth2 Authentication Provider service

In addition to the OpenAPI Authenticator service, this bundle also provides a OAuth2 Authentication Provider service. The reason is that the configuration of the component is very flexible. It supports many different hashes and salt sizes. The property keys used to store the credentials is also variable. The Basic Authentication Provider service provides direct access to the component that is configured as such.

There are also Gogo commands that allow developers and devops to set passwords for users. These commands are in the `oauth2` scope. Type `help oauth2` to see the details.

## Login/Logout Endpoints

To initiate the login and logout redirects, the OAuth2 authenticator has two endpoints:

* `/.openapi/security/<name>/oauth2/login` – Will redirect the caller to the authorization server.
* `/.openapi/security/<name>/oauth2/callback` – Is used for the authorization server to call back to the client. Will authenticate the user with the access token and store the result in the session. The callback will then redirect back the the configurable `finalEndpoint` to report the result back to the user.
* `/.openapi/security/<name>/oauth2/logout` – Remove the user information from the session, if any, and then redirect to the  configurable `finalEndpoint`.

Generally, the redirects take place in a separate window from the main application. This is especially important for single page web apps because usually have state that can get lost when redirected. The configurable `finalEndpoint` is therefore usually a page that closes the window with javascript. This javascript can extract the result from the redirect URI. The OAuth2 authenticator will add a first parameter `?error=ok' to the URI when the sequence was succesful. If it failed the error will be an error code.

## Configuration

The PID of the configuration is `biz.aQute.openapi.oauth2` and the configuration must be a factory. All configuration has metatype and can thus be set in the web console.

|  Key    | Comment                               | Default value                 |
|---------|---------------------------------------|-------------------------------|
| `openapi.name`  | Name of the security provider.        | `basic-auth`                  |
| `provider`  | Supported provider types       | `{ OPENID_CONNECT, GOOGLE, GITHUB }` |
| `authorizationEndpoint`  | URL Authorization server          |                     |
| `tokenEndpoint` | URL Token server   |      |
| `namekey` | The key in the environment properties for the user name  | `email`                 |
| `clientId` | Client id, obtained from the provider  |                 |
| `.clientSecret` | Client secret, obtained from the provider  |                 |
| `scopes` | Default scopes  |                 |
| `finalEndpoint` | Endpoint used for a _login_ or _logout_ session. This is generally an endpoint that closes a window and reports the result back to the main application. | |


[oauth2]: https://tools.ietf.org/html/rfc6749
[openapi]: http://aqute.biz/openapi
[security]: http://aqute.biz/openapi/security.html
[connect]: http://openid.net/connect/