package aQute.openapi.provider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletResponse;

import aQute.lib.exceptions.Exceptions;
import aQute.openapi.util.WWWUtils;

public abstract class OpenAPIBase {

	final Map<Class< ? >,Object> securities = Collections.synchronizedMap(new WeakHashMap<>());

	public interface Codec extends aQute.openapi.codec.api.Codec {
	}

	public static class MimeWrapper {
		public MimeWrapper(String mimeType, byte[] data) {
			this.mimeType = mimeType;
			this.data = data;
		}

		public final String	mimeType;
		public final byte[]	data;
	}

	public enum Method {
		GET, PUT, DELETE, POST, PATCH, HEAD, OPTIONS;
	}

	public static class DoNotTouchResponse extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	public static class Response extends RuntimeException {
		private static final long	serialVersionUID	= 1L;
		final Map<String,String>	headers				= new HashMap<>();;
		final int					resultCode;
		final Object				result;

		public Response(int resultCode, String reason) {
			super(resultCode + ":" + reason);
			this.resultCode = resultCode;
			this.result = null;
		}

		public Response(int resultCode, Object result) {
			super(resultCode + "");
			this.resultCode = resultCode;
			this.result = result;
		}

		public Response(int resultCode, String reason, Throwable ex) {
			super(resultCode + ":" + reason, ex);
			this.resultCode = resultCode;
			this.result = null;
		}

		public Response addHeader(String header, String value) {

			assert !headers.containsKey(header);

			headers.put(header, value);
			return this;
		}

		public Object getResult() {
			return result;
		}
	}

	public static class ConflictResponse extends Response {
		private static final long serialVersionUID = 1L;

		public ConflictResponse(String reason) {
			super(HttpServletResponse.SC_CONFLICT, reason);
		}

		public ConflictResponse(Object result) {
			super(HttpServletResponse.SC_CONFLICT, result);
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

		public BadRequestResponse(Object result) {
			super(HttpServletResponse.SC_BAD_REQUEST, result);
		}

		public BadRequestResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_BAD_REQUEST, reason, ex);
		}
	}

	public enum AuthenticationScheme {
		Basic

	}

	public static class UnauthorizedResponse extends Response {
		private static final long serialVersionUID = 1L;

		public UnauthorizedResponse(Object reason, AuthenticationScheme scheme, String realm,
				Map<String,String> parameters) {
			super(HttpServletResponse.SC_UNAUTHORIZED, reason);
			StringBuilder sb = new StringBuilder();
			sb.append(scheme.toString());
			WWWUtils.property(sb, "realm", realm);
			WWWUtils.properties(sb, parameters);
			headers.put("WWW-Authenticate", sb.toString());
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

		public NotFoundResponse(Object reason) {
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

		public NoContentResponse(Object reason) {
			super(HttpServletResponse.SC_NO_CONTENT, reason);
		}

		public NoContentResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_NO_CONTENT, reason, ex);
		}
	}

	public static class InternalServerErrorResponse extends Response {
		private static final long serialVersionUID = 1L;

		public InternalServerErrorResponse(String reason) {
			super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason);
		}

		public InternalServerErrorResponse(Object reason) {
			super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason);
		}

		public InternalServerErrorResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, reason, ex);
		}
	}

	public static class CreatedResponse extends Response {
		private static final long serialVersionUID = 1L;

		public CreatedResponse(String reason) {
			super(HttpServletResponse.SC_CREATED, reason);
		}

		public CreatedResponse(Object reason) {
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

		public FoundResponse(Object reason) {
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

		public ForbiddenResponse(Object reason) {
			super(HttpServletResponse.SC_FORBIDDEN, reason);
		}

		public ForbiddenResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_FORBIDDEN, reason, ex);
		}
	}

	public static class GoneResponse extends Response {
		private static final long serialVersionUID = 1L;

		public GoneResponse(Object reason) {
			super(HttpServletResponse.SC_GONE, reason);
		}

		public GoneResponse(String reason) {
			super(HttpServletResponse.SC_GONE, reason);
		}

		public GoneResponse(String reason, Throwable ex) {
			super(HttpServletResponse.SC_GONE, reason, ex);
		}
	}

	public abstract static class RedirectResponse extends Response {
		private static final long	serialVersionUID	= 1L;
		private URI					uri;

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

	final String						prefix;
	final ThreadLocal<OpenAPIContext>	contexts	= new ThreadLocal<>();
	final String[]						ops;
	final Class< ? extends OpenAPIBase>	parent;
	protected boolean					validate	= true;
	protected boolean					require		= true;

	public abstract boolean dispatch_(OpenAPIContext context, String[] segments, int index) throws Exception;

	public void before_(OpenAPIContext context) throws Exception {
		contexts.set(context);
	}

	public void after_(OpenAPIContext context) throws Exception {
		contexts.remove();
	}

	protected OpenAPIBase(String prefix, String... ops) {
		this.prefix = prefix;
		this.ops = ops;
		this.parent = null;
	}

	protected OpenAPIBase(String prefix, Class< ? extends OpenAPIBase> parent, String... ops) {
		this.prefix = prefix;
		this.parent = parent;
		this.ops = ops;
	}

	protected OpenAPIContext getOpenAPIContext() {
		return contexts.get();
	}

	public OpenAPIBase.Codec codec_() {
		return OpenAPIRuntime.deflt;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public static Codec createOpenAPICodec() {
		return new CodecWrapper();
	}

	public <T> T instantiate_(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw Exceptions.duck(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> void addDateTimeHandler(Codec codec, Class<T> dateAndOrTimeClass, String pattern) {
		if (dateAndOrTimeClass == Instant.class) {
			DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"));
			codec.addStringHandler(Instant.class, df::format, (s) -> Instant.from(df.parse(s)));
		} else if (dateAndOrTimeClass == Date.class) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			TimeZone utc = TimeZone.getTimeZone("UTC");
			codec.addStringHandler(Date.class, d -> {
				synchronized (df) {
					df.setTimeZone(utc);
					return df.format(d);
				}
			}, (s) -> {
				try {
					synchronized (df) {
						df.setTimeZone(utc);
						return df.parse(s);
					}
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			});
		} else if (TemporalAccessor.class.isAssignableFrom(dateAndOrTimeClass)) {
			Class<TemporalAccessor> temporalAccessor = (Class<TemporalAccessor>) dateAndOrTimeClass;

			try {
				DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				MethodType mt = MethodType.methodType(temporalAccessor, CharSequence.class, DateTimeFormatter.class);
				MethodHandle mh = lookup.findStatic(dateAndOrTimeClass, "parse", mt);
				codec.addStringHandler(temporalAccessor, df::format, (s) -> {
					try {
						return temporalAccessor.cast(mh.invoke(s, df));
					} catch (RuntimeException | Error e) {
						throw e;
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				});
			} catch (NoSuchMethodException | IllegalAccessException e1) {
				throw new RuntimeException(e1);
			}
		} else
			throw new IllegalArgumentException("Not a Date class or TemporalAccess " + dateAndOrTimeClass);
	}

	public boolean hasPermission(String action, String... arguments) throws Exception {
		return getOpenAPIContext().hasPermission(action, arguments);
	}

	public void checkPermission(String action, String... arguments) throws Exception {
		getOpenAPIContext().checkPermission(action, arguments);
	}

	public <T> T getSemanticSecurity(Class<T> type) {
		Object o = securities.get(type);
		if (o == null) {
			o = Proxy.newProxyInstance(type.getClassLoader(), new Class< ? >[] {
					type
			}, new InvocationHandler() {

				@Override
				public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
					String action = method.getName().replace('_', ':');
					String as[] = new String[args.length];
					for (int i = 0; i < as.length; i++)
						as[i] = args[i] == null ? "" : args.toString();

					if (method.getReturnType() == boolean.class)
						return hasPermission(action, as);
					else
						checkPermission(action, as);
					return null;
				}
			});
			securities.put(type, o);
		}
		return type.cast(o);
	}
}
