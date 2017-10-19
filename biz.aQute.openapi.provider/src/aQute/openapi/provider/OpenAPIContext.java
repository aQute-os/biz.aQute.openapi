package aQute.openapi.provider;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.lib.converter.Converter;
import aQute.lib.io.IO;
import aQute.lib.strings.Strings;
import aQute.openapi.provider.OpenAPIBase.Method;
import aQute.openapi.provider.OpenAPIBase.MimeWrapper;
import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import aQute.openapi.util.WWWUtils;

public class OpenAPIContext {
	final static Logger				logger				= LoggerFactory.getLogger(OpenAPIRuntime.class);

	private static final String[]	EMPTY				= new String[0];

	final HttpServletRequest		request;
	final HttpServletResponse		response;
	final OpenAPIRuntime			runtime;
	final Map<String,String>		pathParameters		= new HashMap<>();
	final static Logger				log					= LoggerFactory.getLogger(OpenAPIContext.class);

	private static final String[]	EMPTY_STRING_ARRAY	= new String[0];
	final Dispatcher				dispatcher;
	final int						beginStatus;
	private Method					method;
	private String					operation;
	private List<String>			stack				= new ArrayList<>();
	private List<String>			errors				= null;
	private OpenAPIBase				target;
	Authenticator					authenticator;

	protected OpenAPIContext(OpenAPIRuntime runtime, Dispatcher dispatcher, HttpServletRequest request,
			HttpServletResponse response) {
		this.runtime = runtime;
		this.dispatcher = dispatcher;
		this.request = request;
		this.response = response;
		this.beginStatus = response == null ? 200 : response.getStatus();
	}

	public boolean isMethod(Method method) {
		return method() == method;
	}

	public Method method() {
		if (this.method == null) {
			this.method = Method.valueOf(request.getMethod().toUpperCase());
		}
		return this.method;
	}

	public void pathParameter(String key, String value) {
		pathParameters.put(key, value);
	}

	public void setResult(Object result, int resultCode) throws IOException {
		if (isBeginStatus())
			response.setStatus(resultCode);

		doHeaders();

		if (result != null) {
			if (result instanceof MimeWrapper) {
				MimeWrapper w = (MimeWrapper) result;
				response.setContentType(w.mimeType);
				OutputStream out = getOutputStream();
				out.write(w.data);
			} else {
				String mime = target.codec_().getContentType();
				response.setContentType(mime);
				OutputStream out = getOutputStream();
				try {
					target.codec_().encode(result, out);
				} catch (Exception e) {
					log.error("failed to serialize output for " + operation);
				}
			}
		}
	}

	protected boolean isBeginStatus() {
		return response.getStatus() == beginStatus;
	}

	private void doHeaders() {
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	private OutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	public String parameter(String name) {
		return request.getParameter(name);
	}

	public String[] parameters(String parameter) {
		return request.getParameterValues(parameter);
	}

	public String header(String name) {
		Enumeration<String> e = request.getHeaders(name);
		if (e != null && e.hasMoreElements()) {
			String s = e.nextElement();
			if (e.hasMoreElements()) {
				while (e.hasMoreElements()) {
					s += "," + e.nextElement();
				}
			}
			return s;
		}
		return null;
	}

	public String[] headers(String name) {
		Enumeration<String> e = request.getHeaders(name);
		if (e != null && e.hasMoreElements()) {
			List<String> headers = new ArrayList<>();
			if (e.hasMoreElements()) {
				headers.add(e.nextElement());
			}
			return headers.toArray(new String[] {});
		}
		return null;
	}

	public String path(String id) throws UnsupportedEncodingException {
		String string = pathParameters.get(id);
		if (string == null)
			return null;

		String decoded = URLDecoder.decode(string, "UTF-8");
		return decoded;
	}

	public Long toLong(String value) {
		if (value == null)
			return null;

		return Long.valueOf(value);
	}

	public String toString(String value) {
		return value;
	}

	public <T> Optional<T> optional(T value) {
		if (value == null)
			return Optional.empty();

		return Optional.of(value);
	}

	public OpenAPIBase.Part part(String key) throws IOException, ServletException {
		Part part = request.getPart(key);
		if (part == null) {
			return null;
		}
		return new OpenAPIBase.Part(part);
	}

	public <T> T body(Class<T> type) throws Exception {
		InputStream in = request.getInputStream();
		T t = target.codec_().decode(type, in, request.getContentType(), target::instantiate_);
		return t;
	}

	public <T> List<T> listBody(Class<T> componentType) throws Exception {
		InputStream in = request.getInputStream();
		return target.codec_().decodeList(componentType, in, request.getContentType(), target::instantiate_);
	}

	public void begin(String name) {
		stack.add(name);
	}

	public void begin(int i) {
		stack.add(Integer.toString(i));
	}

	public boolean validate(boolean expression, Object value, String reference, String validation) {
		if (expression)
			return true;

		String format = String.format("%s %s=%s FAILS: %s", Strings.join("/", stack), reference, value, validation);
		if (errors == null)
			errors = new ArrayList<>();

		errors.add(format);
		return false;
	}

	public void end() {
		stack.remove(stack.size() - 1);
		if (stack.isEmpty()) {
			if (errors != null) {
				throw new OpenAPIBase.BadRequestResponse(Strings.join("\n", errors));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> toArray(Class<T> type, String values[]) throws Exception {
		if (values == null)
			return null;

		if (type == String.class)
			return (List<T>) Arrays.asList(values);

		List<T> list = new ArrayList<T>();

		for (int i = 0; i < values.length; i++) {
			list.add(Converter.cnv(type, values[i]));
		}
		return list;
	}

	public boolean in(String value, String... sortedSet) {
		return Arrays.binarySearch(sortedSet, value) >= 0;
	}

	public Long toLongDateTime(String value) {
		if (value == null)
			return null;

		ZonedDateTime parse = ZonedDateTime.parse(value);
		return parse.toInstant().toEpochMilli();
	}

	public Integer toInt(String value) {
		if (value == null)
			return null;

		return Integer.valueOf(value);
	}

	public Boolean toBoolean(String value) {
		if (value == null)
			return null;

		return Boolean.valueOf(value);
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void report(Exception e) {
		logger.info("{} {} {} {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr(),
				e.getMessage());
	}

	public String[] csv(String value) {
		if (value == null)
			return EMPTY;

		return value.split(",");
	}

	public String[] ssv(String value) {
		if (value == null)
			return EMPTY;

		return value.split(" +");
	}

	public String[] tsv(String value) {
		if (value == null)
			return EMPTY;

		return value.split("\t");
	}

	public String[] pipes(String value) {
		if (value == null)
			return EMPTY;

		return value.split("|");
	}

	public Double toDouble(String value) {
		if (value == null)
			return null;

		return Double.valueOf(value);
	}

	public boolean require(Object value, String name) {
		return validate(value != null, value, name, " required but not set");
	}

	public char[] toPassword(String value) {
		if (value == null)
			return null;

		return value.toCharArray();
	}

	public LocalDate toDate(String parameter) {
		return LocalDate.parse(parameter);
	}

	public String getUser() {
		return authenticator != null ? authenticator.getUser() : null;
	}

	public String getOriginalIP() {
		return (String) request.getSession().getAttribute("ip");
	}

	public void setUser(String user) {
		request.getSession().setAttribute("user", user);
		request.getSession().setAttribute("ip", request.getRemoteHost());
	}

	protected void securityException(String name, String message) {
		throw new SecurityException(name + ":" + message);
	}

	void setTarget(OpenAPIBase base) {
		this.target = base;
	}

	public void copy(InputStream in, String mime) throws IOException {
		response.setContentType(mime);
		doHeaders();
		IO.copy(in, response.getOutputStream());
	}

	public OpenAPIContext or() {
		if (authenticator != null)
			authenticator.or();
		return this;
	}

	public OpenAPIContext verify(OpenAPISecurityDefinition def, String... args) throws Exception {
		if (authenticator == null)
			this.authenticator = new Authenticator();

		Authentication auth = null;
		OpenAPIAuthenticator securityProvider = dispatcher.getSecurityProvider(def.id, def.type);
		if (securityProvider != null) {
			auth = securityProvider.authenticate(request, response, def);
		} else {
			OpenAPIRuntime.logger.error("Failing authentication because missing provider " + def.name);
		}

		authenticator.authenticate(auth, args);
		return this;
	}

	public void verify() throws Exception {
		if (authenticator != null)
			authenticator.verify();
	}

	public boolean hasPermission(String action, String... arguments) throws Exception {
		OpenAPISecurityEnvironment security = runtime.security;
		return security.hasPermission(action, arguments);
	}

	public void checkPermission(String name, String... resource) throws Exception {
		if (!hasPermission(name, resource))
			throw new SecurityException("Unauthorized " + name + ":" + of(resource).collect(joining(":")));
	}

	public String path() {
		return request.getPathInfo();
	}

	public <T> T call(Callable<T> callable) throws Exception {
		if (authenticator == null)
			return callable.call();

		OpenAPISecurityEnvironment security = runtime.security;
		return security.dispatch(authenticator.user, null, null, callable);
	}

	public boolean isEncrypted() {
		return request.getScheme().equalsIgnoreCase("https");
	}

	public String getURL() {
		StringBuffer url = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString != null) {
			url.append("?");
			url.append(queryString);
		}
		return url.toString();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getOperation() {
		return operation;
	}

	public String formData(String name) {

		assert request.getContentType().equals(WWWUtils.APPLICATION_X_WWW_FORM_URLENCODED);

		String[] parameterValues = request.getParameterValues(name);

		if (parameterValues == null)
			return null;

		if (parameterValues.length == 0)
			return null;

		if (parameterValues.length == 1)
			return parameterValues[0];

		StringBuilder sb = new StringBuilder();
		String del = "";

		for (String s : parameterValues) {
			sb.append(del);
			escapeCommas(sb, s);
			del = ",";
		}
		return sb.toString();
	}

	public String[] formDataArray(String name) {

		assert request.getContentType().equals(WWWUtils.APPLICATION_X_WWW_FORM_URLENCODED);

		String[] parameterValues = request.getParameterValues(name);

		if (parameterValues == null)
			return null;

		return parameterValues;

	}

	protected void escapeCommas(StringBuilder sb, String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == ',')
				sb.append('\\');
			sb.append(c);
		}
	}

	public String[] csv(String[] parameters) {
		return split(parameters, ",");
	}

	private String[] split(String[] parameters, String split) {

		if (parameters == null)
			return null;

		List<String> result = new ArrayList<>();

		for (String s : parameters) {
			String parts[] = s.split(split);
			for (String p : parts) {
				result.add(p);
			}
		}
		return result.toArray(EMPTY_STRING_ARRAY);
	}

	public String[] pipes(String[] parameters) {
		return split(parameters, "|");
	}

	public String[] tsv(String[] parameters) {
		return split(parameters, "\t");
	}

	public String[] ssv(String[] parameters) {
		return split(parameters, " ");
	}

	public OpenAPIBase.MimeWrapper wrap(String mimeType, byte[] data) {
		return new OpenAPIBase.MimeWrapper(mimeType, data);
	}

	public OpenAPIBase.MimeWrapper wrap(String mimeType, InputStream in) throws IOException {
		return wrap(mimeType, IO.read(in));
	}

	public OpenAPIBase.MimeWrapper wrap(String mimeType, File in) throws IOException {
		return wrap(mimeType, IO.read(in));
	}
}
