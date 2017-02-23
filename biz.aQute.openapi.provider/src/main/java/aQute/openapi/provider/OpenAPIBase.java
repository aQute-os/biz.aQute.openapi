package aQute.openapi.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public abstract class OpenAPIBase {

	public enum Method {
		GET, PUT, DELETE, POST, PATCH, HEAD, OPTIONS;
	}
	
	public static class Response extends RuntimeException {
		private static final long	serialVersionUID	= 1L;
		final Map<String, String>	headers				= new HashMap<>();;
		final int					resultCode;

		public Response(int resultCode, String reason) {
			super(resultCode + ":" + reason);
			this.resultCode = resultCode;
		}

		public Response(int resultCode, String reason, Throwable ex) {
			super(resultCode + ":" + reason, ex);
			this.resultCode = resultCode;
		}

		public Response addHeader(String header, String value) {

			assert !headers.containsKey(header);

			headers.put(header, value);
			return this;
		}
	}

	public static class ConflictResponse extends Response {
		private static final long serialVersionUID = 1L;

		public ConflictResponse(String reason) {
			super(HttpServletResponse.SC_CONFLICT, reason);
		}
		
		public ConflictResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_CONFLICT, reason, ex);
		}
	}

	public static class BadRequestResponse extends Response {
		private static final long serialVersionUID = 1L;

		public BadRequestResponse(String reason) {
			super(HttpServletResponse.SC_BAD_REQUEST, reason);
		}
		public BadRequestResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_BAD_REQUEST, reason, ex);
		}
	}

	public static class UnauthorizedResponse extends Response {
		private static final long serialVersionUID = 1L;

		public UnauthorizedResponse(String reason) {
			super(HttpServletResponse.SC_UNAUTHORIZED, reason);
		}
		public UnauthorizedResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_UNAUTHORIZED, reason, ex);
		}
	}

	public static class NotFoundResponse extends Response {
		private static final long serialVersionUID = 1L;

		public NotFoundResponse(String reason) {
			super(HttpServletResponse.SC_NOT_FOUND, reason);
		}
		public NotFoundResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_NOT_FOUND, reason, ex);
		}
	}

	public static class NoContentResponse extends Response {
		private static final long serialVersionUID = 1L;

		public NoContentResponse(String reason) {
			super(HttpServletResponse.SC_NO_CONTENT, reason);
		}
		public NoContentResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_NO_CONTENT, reason, ex);
		}
	}

	public static class InternalServerErrorResponsse extends Response {
		private static final long serialVersionUID = 1L;

		public InternalServerErrorResponsse(String reason) {
			super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason);
		}
		public InternalServerErrorResponsse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason, ex);
		}
	}

	public static class CreatedResponse extends Response {
		private static final long serialVersionUID = 1L;

		public CreatedResponse(String reason) {
			super(HttpServletResponse.SC_CREATED, reason);
		}
		public CreatedResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_CREATED, reason, ex);
		}
	}

	public static class FoundResponse extends Response {
		private static final long serialVersionUID = 1L;

		public FoundResponse(String reason) {
			super(HttpServletResponse.SC_FOUND, reason);
		}
		public FoundResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_FOUND, reason, ex);
		}
	}

	public static class ForbiddenResponse extends Response {
		private static final long serialVersionUID = 1L;

		public ForbiddenResponse(String reason) {
			super(HttpServletResponse.SC_FORBIDDEN, reason);
		}
		public ForbiddenResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_FORBIDDEN, reason, ex);
		}
	}

	public static class GoneResponse extends Response {
		private static final long serialVersionUID = 1L;

		public GoneResponse(String reason) {
			super(HttpServletResponse.SC_GONE, reason);
		}
		public GoneResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_GONE, reason, ex);
		}
	}

	public abstract static class RedirectResponse extends Response {
		private static final long serialVersionUID = 1L;
		private URI uri;

		public RedirectResponse(URI uri, int code, String reason) {
			super(code, reason);
			this.uri = uri;
		}
		
		public URI getUri() {
			return uri;
		}
	}
	
	public static class MovedTemporarilyResponse extends RedirectResponse {
		private static final long serialVersionUID = 1L;

		public MovedTemporarilyResponse(URI uri, String reason) {
			super(uri, HttpServletResponse.SC_MOVED_TEMPORARILY, reason);
		}
	}
	public static class MovedPermanentlyResponse extends RedirectResponse {
		private static final long serialVersionUID = 1L;

		public MovedPermanentlyResponse(URI uri, String reason) {
			super(uri, HttpServletResponse.SC_MOVED_PERMANENTLY, reason);
		}
	}

	public static class TemporaryRedirectResponse extends RedirectResponse {
		private static final long serialVersionUID = 1L;

		public TemporaryRedirectResponse(URI uri, String reason) {
			super(uri, HttpServletResponse.SC_TEMPORARY_REDIRECT, reason);
		}
	}

	public static class Part {
		final javax.servlet.http.Part part;

		Part(javax.servlet.http.Part part) {
			this.part = part;
		}

		public String getContentType() {
			return part.getContentType();
		}

		public String getHeader(String arg0) {
			return part.getHeader(arg0);
		}

		public Collection<String> getHeaderNames() {
			return part.getHeaderNames();
		}

		public Collection<String> getHeaders(String arg0) {
			return part.getHeaders(arg0);
		}

		public InputStream getInputStream() throws IOException {
			return part.getInputStream();
		}

		public String getName() {
			return part.getName();
		}

		public long getSize() {
			return part.getSize();
		}

		public String getSubmittedFileName() {
			return part.getSubmittedFileName();
		}

	}

	public static class DTO extends org.osgi.dto.DTO {

	}

	final String prefix;
	final ThreadLocal<OpenAPIContext> contexts = new ThreadLocal<>();

	public abstract boolean dispatch_(OpenAPIContext context, String[] segments,
			int index) throws Exception;

	public void before_(OpenAPIContext context) throws Exception {
		contexts.set(context);
	}

	public void after_(OpenAPIContext context) throws Exception {
		contexts.remove();
	}

	protected OpenAPIBase(String prefix) {
		this.prefix = prefix;
	}

	protected OpenAPIContext getOpenAPIContext() {
		return contexts.get();
	}
}
