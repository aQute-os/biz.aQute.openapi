package aQute.openapi.provider.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiFunction;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.bnd.header.Parameters;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.strings.Strings;
import aQute.openapi.provider.CORS;
import aQute.openapi.provider.OpenAPIRuntime;
import aQute.openapi.provider.resources.ResourceDomain.Result;
import aQute.openapi.provider.resources.ResourceDomain.ResultCode;

@Component(service = Servlet.class)
public class ResourceDomainServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	final OpenAPIRuntime		runtime;
	final ResourceDomain		server;

	@Reference
	CORS						cors;

	public ResourceDomainServlet(OpenAPIRuntime runtime, ResourceDomain resources) {
		this.runtime = runtime;
		this.server = resources;

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doService(req, rsp, rsp.getOutputStream());
	}

	@Override
	public void doHead(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doService(req, rsp, null);
	}

	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		try {
			String path = req.getPathInfo();
			if (path == null) {
				rsp.setStatus(404);
				return;
			}

			Result resource = server.getResource(path, null, null);
			if (resource.action == ResultCode.UNKNOWN) {
				rsp.setStatus(404);
				return;
			}

			cors.doOptions(req, rsp, "GET", "HEAD");
			cors.fixup(req, rsp);

		} catch (Exception e) {
			throw Exceptions.duck(e);
		}
	}

	void doService(HttpServletRequest req, HttpServletResponse rsp, OutputStream out) {
		try {
			String path = req.getPathInfo();
			if (path == null) {
				rsp.setStatus(404);
				return;
			}

			BiFunction<String,Long,Boolean> doConditionally;

			if (req.getHeader("If-None-Match") != null) {
				String etag[] = Strings.split(req.getHeader("If-None-Match")).toArray(new String[0]);

				doConditionally = (tag, modified) -> {
					if (tag == null)
						return true;

					return !Strings.in(etag, tag);
				};
			} else if (req.getHeader("If-Match") != null) {
				String etag[] = Strings.split(req.getHeader("If-Match")).toArray(new String[0]);
				doConditionally = (tag, modified) -> {
					if (tag == null)
						return true;
					return Strings.in(etag, tag);
				};
			} else if (req.getHeader("If-Modified-Since") != null) {
				doConditionally = (tag, modified) -> {
					return modified < req.getDateHeader("If-Modified-Since");
				};
			} else {
				doConditionally = (tag, modified) -> true;
			}

			String[] compressions = compression(req);

			Result resource = server.getResource(path, doConditionally, out, compressions);

			switch (resource.action) {
				default :
				case COPIED :
					if (resource.chosenCompressionAlgorithm != null) {
						rsp.setHeader("Content-Encoding", resource.chosenCompressionAlgorithm);
						rsp.setHeader("Vary", "Content-Encoding");
					}
					if (resource.details.uncached) {
						rsp.setHeader("Cache-Control", "no-cache");
					} else {
						rsp.setHeader("Cache-Control", "public, max-age=" + resource.details.maxAge);
					}
					rsp.setContentLength(resource.details.length);
					rsp.setHeader("ETag", resource.details.etag);
					rsp.setDateHeader("Last-Modified", resource.details.holder.modified);
					rsp.setStatus(200);
					break;

				case UNMODIFIED :
					rsp.setHeader("ETag", resource.details.etag);
					rsp.setDateHeader("Last-Modified", resource.details.holder.modified);
					rsp.setStatus(304);
					break;

				case UNKNOWN :
					rsp.setStatus(404);
					break;

			}
			cors.fixup(req, rsp);
		} catch (Exception e) {
			rsp.setStatus(500);
		}
	}

	protected String[] compression(HttpServletRequest req) {
		Parameters acceptEncoding = new Parameters(req.getHeader("Accept-Encoding"));
		String[] compressions = acceptEncoding.keySet().toArray(new String[acceptEncoding.size()]);
		return compressions;
	}

}
