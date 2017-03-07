package aQute.openapi.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import aQute.openapi.security.apikey.api.APIKeyDTO;
import aQute.openapi.security.apikey.api.APIKeyProvider;
import aQute.openapi.security.basic.api.BasicDTO;
import aQute.openapi.security.basic.api.BasicProvider;
import aQute.openapi.security.oauth2.api.OAuth2DTO;
import aQute.openapi.security.oauth2.api.OAuth2Provider;

public class OpenAPIContext {
	private static final String[]	EMPTY			= new String[0];

	final HttpServletRequest		request;
	final HttpServletResponse		response;
	final OpenAPIRuntime			runtime;
	final Map<String,String>		pathParameters	= new HashMap<>();
	final static Logger				log				= LoggerFactory.getLogger(OpenAPIContext.class);
	private String					operation;
	private Method					method;
	private List<String>			stack			= new ArrayList<>();
	private List<String>			errors			= null;
	private OpenAPIBase				target;

	protected OpenAPIContext(OpenAPIRuntime runtime, HttpServletRequest request, HttpServletResponse response) {
		this.runtime = runtime;
		this.request = request;
		this.response = response;
		this.method = Method.valueOf(request.getMethod().toUpperCase());
	}

	public boolean isMethod(Method method) {
		return this.method == method;
	}

	public void pathParameter(String key, String value) {
		pathParameters.put(key, value);
	}

	public void setResult(Object result, int resultCode) throws IOException {
		if (response.getStatus() == 0)
			response.setStatus(resultCode);

		doHeaders();

		if (result != null) {

			OutputStream out = getOutputStream();
			try {
				String mime = target.codec_().encode(result, out);
				response.setContentType(mime);
			} catch (Exception e) {
				log.error("failed to serialize output for " + operation);
			}
		}
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

	public void begin(String name) {
		stack.add(name);
	}

	public void begin(int i) {
		stack.add(Integer.toString(i));
	}

	public void validate(boolean expression, Object value, String reference, String validation) {
		if (expression)
			return;

		String format = String.format("%s %s=%s FAILS: %s", Strings.join("/", stack), reference, value, validation);
		if (errors == null)
			errors = new ArrayList<>();

		errors.add(format);
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
	public <T> T[] toArray(Class<T> type, String values[]) throws Exception {
		if (values == null)
			return null;

		if (type == String.class)
			return (T[]) values;

		T[] array = (T[]) Array.newInstance(type, values.length);

		for (int i = 0; i < values.length; i++) {
			array[i] = Converter.cnv(type, values[i]);
		}
		return array;
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
		e.printStackTrace();
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

	public void require(Object value, String name) {
		validate(value != null, value, name, " required but not set");
	}

	public char[] toPassword(String value) {
		if (value == null)
			return null;

		return value.toCharArray();
	}

	public OffsetDateTime toDateTime(String parameter) {
		return OffsetDateTime.parse(parameter);
	}

	public LocalDate toDate(String parameter) {
		return LocalDate.parse(parameter);
	}

	public String getUser() {
		return (String) request.getSession().getAttribute("user");
	}

	public String getOriginalIP() {
		return (String) request.getSession().getAttribute("ip");
	}

	public void setUser(String user) {
		request.getSession().setAttribute("user", user);
		request.getSession().setAttribute("ip", request.getRemoteHost());
	}

	public void verify(APIKeyDTO dto, String headerValue) {
		APIKeyProvider apiKey = runtime.apiKey;
		if (apiKey == null) {
			securityException(dto.name, "No API Key Security Provider found");
			return;
		}

		if (!apiKey.check(this, dto, operation, headerValue))
			securityException(dto.name, "Unauthorized key");
	}

	public void verify(BasicDTO dto) {
		BasicProvider basic = runtime.basic;
		if (basic == null) {
			securityException(dto.name, "No Basic Security Provider found");
			return;
		}
		if (!basic.check(this, dto, operation))
			securityException(dto.name, "Could not authenticate");
	}

	public void verify(OAuth2DTO dto, String... scopes) {
		OAuth2Provider oauth2 = runtime.oath2;
		if (oauth2 == null) {
			securityException(dto.name, "No OAuth2 Provider found");
			return;
		}

		if (!oauth2.check(this, dto, operation, scopes)) {
			securityException(dto.name, "Scopes not authorized " + scopes);
		}
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

}
