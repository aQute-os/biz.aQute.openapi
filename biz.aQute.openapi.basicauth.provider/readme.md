# Basic Authentication

This project is part of the [OpenAPI Suite][openapi] and provides an _authenticator_ for [Basic Authentication][basic]. The architecture for authenticators is explained in the [security section][security].

## Operation

Basic authentication is an authentication model where the browser provides a user id and a password. This bundle will extract these credentials and verify it against some user properties in the OpenAPI Environment service.

Passwords are not stored in the clear, instead, a digest of the password is stored. To prevent dictionary attacks, a password specific salt can be used. This authenticator supports different digest algorithms and salt sizes.

The digest algorithm is as follows:

* Convert the password to UTF-8 byte array
* If set to `PLAIN` then this array is stored. (Not secure.)
* Create a message digester for the given algorithm
* If a salt is specified, create a random salt of the given length
* Add this salt to the digester
* Add the UTF-8 byte array to the digester
* Store the salt, if any, in the security environment credentials. The key is configurable with the `saltKey` property but by default `password.salt`
* Store the password in the security environment credentials. The key is configurable with the `pwKey` property but by default `password.digest`

During verification, the compare function is constant time to prevent leakage of the password based on time measuring attacks.

The Basic Authentication authenticator will remember the credential verification in the session to speed up future requests.

## The Basic Authentication Provider service

In addition to the OpenAPI Authenticator service, this bundle also provides a Basic Authentication Provider service. The reason is that the configuration of the component is very flexible. It supports many different hashes and salt sizes. The property keys used to store the credentials is also variable. The Basic Authentication Provider service provides direct access to the component that is configured as such.

There are also Gogo commands that allow developers and devops to set passwords for users. These commands are in the `basiC` scope. Type `help basic` to see the details.

## Login/Logout Endpoints

Each authenticator has an assigned login and logout endpoint intended to be used by single page web apps. If the `login` is triggered then a Basic Authenticator will authenticate as usual. If no credentials are given, a 403 Unauthorized response is returned. If the user can be succesfully authenticated, the browser is redirected to the _closing endpoint_, if set. This closing endpoint can be used to close a window and/or get a notification to a single page web app.

A logout will remove any session information about the user.


## Configuration

The PID of the configuration is `biz.aQute.openapi.basicauth` and the configuration must be a factory. All configuration has metatype and can thus be set in the web console.

|  Key    | Comment                               | Default value                 |
|---------|---------------------------------------|-------------------------------|
| `openapi.name`  | Name of the security provider.        | `basic-auth`                  |
| `hash`  | Algorithm to hash the passwords       | `{ PLAIN, SHA,SHA_256,SHA_512 }` |
| `salt`  | number of salt random bytes to use for the hashing           | `int`                    |
| `idkey` | OpenAPI Environment property key for the user name, when this is empty, the user identity is used.   | `email`                 |
| `pwkey` | The key in the environment credentials for a hashed password  | `password`                 |
| `realm` | The realm used when requesting the browser for a login | `Basic` |
| `requireEncrypted`| Force the use of an encrypted connection. (Highly recommended since Basic Authentication is not safe without it.| `true` |
| `reportingEndpoint` | Endpoint used for a _login_ or _logout_ session. This is generally an endpoint that closes a window and reports the result back to the main application. | |


[basic]: https://tools.ietf.org/html/rfc2617
[openapi]: http://aqute.biz/openapi
[security]: http://aqute.biz/openapi/security.html