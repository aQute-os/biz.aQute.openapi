package aQute.openapi.provider.cors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import aQute.lib.strings.Strings;
import aQute.libg.glob.Glob;
import aQute.openapi.provider.CORS;

/**
 * To test:
 * https://www.test-cors.org/#?client_method=PUT&client_credentials=false&client_postdata=%7B%7D&server_url=http%3A%2F%2Flocalhost%3A8080%2Fv2%2Fuser%2Ffoo&server_enable=true&server_status=200&server_credentials=false&server_tabs=remote
 */
public class CORSImplementation implements CORS {
	Set<Glob>	listOfOrigins			= new HashSet<>();
	Set<String>	listOfExposedHeaders	= new HashSet<>();
	Set<Glob>	listOfHeaders			= new HashSet<>();
	boolean		supportCredentials		= false;
	int			maxAge;
	Logger		logger;

	public CORSImplementation(Logger logger, String[] listOfOrigins, String[] listOfExposedHeaders,
			String[] listOfHeaders, boolean supportCredentials, int maxAge) {
		this.logger = logger;
		this.listOfOrigins = toGlobs(listOfOrigins);
		this.listOfExposedHeaders = listOfExposedHeaders == null ? Collections.emptySet()
				: Stream.of(listOfExposedHeaders).collect(Collectors.toSet());
		this.listOfHeaders = toGlobs(listOfHeaders);
		this.supportCredentials = supportCredentials;
		this.maxAge = maxAge;
	}

	/**
	 * Simple Cross-Origin Request
	 */

	public boolean fixup(HttpServletRequest request, HttpServletResponse response) {

		// If the Origin header is not present terminate this set of steps.
		// The request is outside the scope of this specification.

		String origin = request.getHeader("Origin");
		if (origin == null) {
			logger.debug("{} no expected Origin header set", this);
			return false;
		}

		// 2. If the value of the Origin header is not a case-sensitive
		// match for any of the values in list of origins do not set
		// any additional headers and terminate this set of steps.

		boolean wildcard = listOfOrigins.isEmpty();

		if (!wildcard && !in(listOfOrigins, origin)) {
			logger.warn("{} Invalid origin {}, allowed {}", this, origin, listOfOrigins);
			return false;
		}

		// If the resource supports credentials add a single
		// Access-Control-Allow-Origin header,
		// with the value of the Origin header as value, and add a single
		// Access-Control-Allow-Credentials header with the case-sensitive
		// string "true" as value.

		String allowOrigin = response.getHeader("Access-Control-Allow-Origin");

		if (supportCredentials) {
			if (allowOrigin == null)
				response.addHeader("Access-Control-Allow-Origin", origin);
			String allowCredentials = response.getHeader("Access-Control-Allow-Credentials");
			if (allowCredentials == null)
				response.addHeader("Access-Control-Allow-Credentials", "true");
		} else {

			// Otherwise, add a single Access-Control-Allow-Origin header, with
			// either the value of the Origin header or the string "*" as value.

			if (allowOrigin == null)
				response.addHeader("Access-Control-Allow-Origin", origin);
		}

		// If the list of exposed headers is not empty add one or more
		// Access-Control-Expose-Headers headers, with as values the header
		// field names given in the list of exposed headers.

		if (!listOfExposedHeaders.isEmpty()) {
			for (String h : listOfExposedHeaders) {
				response.addHeader("Access-Control-Expose-Headers", h);
			}
		}
		return true;
	}

	/**
	 * This method is called for any resource that has a method defined. The
	 * methods are passed as a parameter. This is primarily used as a preflight
	 * check for the CORS protocol
	 * 
	 * @param methods an array of methods for this URL
	 * @return true if found
	 * @throws IOException
	 */
	public boolean doOptions(HttpServletRequest request, HttpServletResponse response, String... methods)
			throws IOException {

		if (!"OPTIONS".equals(request.getMethod()))
			return false;

		return preflight(request, response, methods);
	}

	private boolean preflight(HttpServletRequest request, HttpServletResponse response, String... methods)
			throws IOException {

		// 1. If the Origin header is not present terminate this set of steps.

		String origin = request.getHeader("Origin");
		if (origin == null) {
			logger.debug("{} no expected Origin header set", request);
			return false;
		}

		// 2. If the value of the Origin header is not a case-sensitive
		// match for any of the values in list of origins do not set
		// any additional headers and terminate this set of steps.

		boolean wildcard = listOfOrigins.isEmpty();

		if (!wildcard && !in(listOfOrigins, origin)) {
			logger.warn("{} Invalid origin {}, allowed {}", request, origin, listOfOrigins);
			return false;
		}

		// Let method be the value as result of parsing the
		// Access-Control-Request-Method header.
		// If there is no Access-Control-Request-Method header or if parsing
		// failed, do not set
		// any additional headers and terminate this set of steps. The request
		// is outside the scope of this specification

		String method = request.getHeader("Access-Control-Request-Method");
		if (method == null) {
			logger.warn("{} Missing expected CORS header  Access-Control-Request-Method", request);
			response.setStatus(400);
			return true;
		}

		// If method is not a case-sensitive match for any of the values in
		// list of methods do not set any additional headers and terminate this
		// set of steps.

		if (!Strings.in(methods, method.trim().toUpperCase())) {
			logger.warn("{} Not an allowed method {}", request, method);
			response.setStatus(400);
			return true;
		}

		// Let header field-names be the values as result of parsing the
		// Access-Control-Request-Headers headers.
		// If there are no Access-Control-Request-Headers headers let
		// header field-names be the empty list.
		// If parsing failed do not set any additional headers and terminate
		// this set of steps. The request is outside the scope of this
		// specification.

		List<String> headers;
		String rawRequestHeaders = request.getHeader("Access-Control-Request-Headers");
		if (rawRequestHeaders == null)
			headers = Collections.emptyList();
		else {
			rawRequestHeaders = rawRequestHeaders.toLowerCase();
			headers = Strings.split(rawRequestHeaders);
		}

		// If any of the header field-names is not a ASCII case-insensitive
		// match for any of the values in list of headers do not set any
		// additional headers and terminate this set of steps.

		header: for (String h : headers) {

			if (in(listOfHeaders, h))
				continue header;

			logger.warn("{} Not an allowed header {}, allowed {}", request, h, listOfHeaders);
			response.setStatus(400);
			return true;
		}

		// If the resource supports credentials add a single
		// Access-Control-Allow-Origin header,
		// with the value of the Origin header as value, and add a single
		// Access-Control-Allow-Credentials
		// header with the case-sensitive string "true" as value.

		if (supportCredentials) {
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}

		// Optionally add a single Access-Control-Max-Age header with as value
		// the amount of seconds the user agent is allowed to cache the result
		// of the request.

		if (maxAge > 0) {
			response.setIntHeader("Access-Control-Max-Age", maxAge);
		}

		// Add one or more Access-Control-Allow-Methods headers consisting of (a
		// subset of) the list of methods.

		Arrays.sort(methods);
		response.setHeader("Access-Control-Allow-Methods", Strings.join(methods));

		// If each of the header field-names is a simple header and none is
		// Content-Type, this step may be skipped.

		boolean eachSimpleHeader = headers.stream().allMatch(this::isSimpleHeader);
		boolean noneIsContentType = !headers.contains("content-type");
		boolean skipStep = eachSimpleHeader && noneIsContentType;

		if (!skipStep) {

			// Add one or more Access-Control-Allow-Headers headers consisting
			// of (a subset of) the list of headers.

			response.setHeader("Access-Control-Allow-Headers", Strings.join(headers));
		}
		response.setStatus(204);
		return true;
	}

	private boolean in(Set<Glob> globs, String term) {
		for (Glob g : globs) {
			if (g.matcher(term).matches())
				return true;
		}
		return false;
	}

	/*
	 * A header is said to be a simple header if the header field name is an
	 * ASCII case-insensitive match for Accept, Accept-Language, or
	 * Content-Language or if it is an ASCII case-insensitive match for
	 * Content-Type and the header field value media type (excluding parameters)
	 * is an ASCII case-insensitive match for application/x-www-form-urlencoded,
	 * multipart/form-data, or text/plain.
	 * @param lowerCaseHeader
	 * @return
	 */
	private boolean isSimpleHeader(String lowerCaseHeader) {
		switch (lowerCaseHeader) {
			case "accept" :
			case "accept-language" :
			case "content-language" :
			case "content-type" :
				return true;

			default :
				return false;
		}
	}

	private Set<Glob> toGlobs(String[] listOfOrigins) {
		return Stream.of(listOfOrigins).map(Glob::new).collect(Collectors.toSet());
	}

}
