package aQute.openapi.provider;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CORS {

	boolean fixup(HttpServletRequest request, HttpServletResponse response) throws Exception;

	/**
	 * This method is called for any resource that has a method defined. The
	 * allowed methods are passed as a parameter. This is primarily used as a
	 * preflight check for the CORS protocol. I.e. it will return the proper
	 * information for CORS preflights in the response.
	 * 
	 * @param methods an array of methods for this URL
	 * @return true if found
	 * @throws IOException
	 */
	boolean doOptions(HttpServletRequest request, HttpServletResponse response, String... methods) throws Exception;

}
