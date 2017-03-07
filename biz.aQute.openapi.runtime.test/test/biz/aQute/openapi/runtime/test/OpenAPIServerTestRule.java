package biz.aQute.openapi.runtime.test;

import java.net.URI;
import java.net.URL;
import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.http.HttpClient;
import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIRuntime;

public class OpenAPIServerTestRule implements TestRule
{
   public URI uri;
   public Server server = new Server(0);
   public ServletHandler handler;
   public OpenAPIRuntime runtime = new OpenAPIRuntime();
   public HttpClient http = new HttpClient();

   @Override
   public Statement apply(Statement statement, Description description)
   {


      return new Statement()
      {

         @Override
         public void evaluate() throws Throwable
         {
            try
            {
               handler = new ServletHandler();
               server.setHandler(handler);
               server.start();

               while (!(server.isStarted() || server.isRunning()))
                  Thread.sleep(100);

               runtime.http = new HttpService()
               {

                  @Override
                  public void unregister(String alias)
                  {}

                  @Override
                  public void registerServlet(String alias, Servlet servlet,
                           @SuppressWarnings("rawtypes") Dictionary initparams, HttpContext context)
                           throws ServletException, NamespaceException
                  {
                     handler.addServletWithMapping(new ServletHolder(servlet), (alias + "/*"));
                  }

                  @Override
                  public void registerResources(String alias, String name, HttpContext context)
                           throws NamespaceException
                  {

                  }

                  @Override
                  public HttpContext createDefaultHttpContext()
                  {
                     return null;
                  }
               };

               uri = server.getURI();
               statement.evaluate();
            }
            finally
            {
               server.stop();
               server.join();
            }
         }
      };
   }

   public <X extends OpenAPIBase> X add(X x) throws Exception
   {
      runtime.add(x);
      return x;

   }

   public String put(String path, String payload) throws Exception
   {
      URL uri = this.uri.resolve(path).toURL();
      return http.build().put().upload(payload.replace('\'', '"')).get(String.class).go(uri).replace('"', '\'');
   }
}
