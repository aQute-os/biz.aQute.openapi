package aQute.openapi.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.VersionRange;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.Run;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors.PackageRef;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.URLResource;
import aQute.bnd.service.Strategy;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;

public class DummyFramework extends Assert implements AutoCloseable {

	public static class Rule implements TestRule {
		public DummyFramework framework;

		@Override
		public Statement apply(Statement statement, Description arg1) {
			try {
				framework = new DummyFramework();
				statement.evaluate();
			} catch (Throwable e) {
				e.printStackTrace();
				Exceptions.duck(e);
			} finally {
				try {
					framework.close();;
				} catch (Exception e) {
					// ignore
				}
			}
			return statement;
		}

	}

	ExecutorService							executor	= Executors.newCachedThreadPool();
	public final List<ServiceTracker<?, ?>>	trackers	= new ArrayList<>();
	public final Jar						bin_test;
	public final Framework					framework;
	public final BundleContext				context;
	public Workspace						workspace;
	public Project							project;

	public DummyFramework() {
		try {
			bin_test = new Jar(IO.getFile("bin_test"));
			String extra = getExtra();

			Map<String, String> props = new HashMap<>();
			props.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, extra);

			File storage = IO.getFile("generated/fw");
			IO.delete(storage);

			FrameworkFactory factory = getFactory();
			props.put(Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath());
			props.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
			framework = factory.newFramework(props);
			framework.init();
			framework.start();
			this.context = framework.getBundleContext();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private FrameworkFactory getFactory() throws Exception {
		ClassLoader cl = getClass().getClassLoader();
		Enumeration<URL> resources = cl.getResources("META-INF/services/" + FrameworkFactory.class.getName());
		if (resources == null || !resources.hasMoreElements())
			throw new FileNotFoundException("No Framework found on classpath");

		URL url = resources.nextElement();
		String name = IO.collect(url.openStream());
		String parts[] = name.trim().split("\\s*\r?\n\\s*");
		for (String part : parts) {
			if (part.isEmpty())
				continue;

			if (part.startsWith("#"))
				continue;

			@SuppressWarnings("unchecked")
			Class<FrameworkFactory> factory = (Class<FrameworkFactory>) cl.loadClass(part);
			FrameworkFactory instance = factory.newInstance();
			return instance;
		}
		throw new FileNotFoundException("No Framework found on classpath");
	}

	@SuppressWarnings("resource")
	private String getExtra() throws Exception {
		Analyzer a = new Analyzer(); // don't close it
		for (Container c : getProject().getBuildpath()) {
			assertNull(c.getError());
			a.addClasspath(c.getFile());
		}
		a.setJar(bin_test);
		a.calcManifest();
		StringBuilder extra = new StringBuilder();
		String del = "";
		for (Entry<PackageRef, Attrs> e : a.getImports().entrySet()) {
			extra.append(del);
			extra.append(e.getKey().getFQN());
			String v = e.getValue().getVersion();
			if (v != null) {
				VersionRange vr = VersionRange.valueOf(v);
				extra.append(";version=").append(vr.getLeft());
			}
			del = ",";
		}
		for (Entry<PackageRef, Attrs> e : a.getContained().entrySet()) {
			extra.append(del);
			extra.append(e.getKey().getFQN());
			String v = e.getValue().getVersion();
			if (v != null) {
				VersionRange vr = VersionRange.valueOf(v);
				extra.append(";version=").append(vr.getRight());
			}
			del = ",";
		}
		return extra.toString();
	}

	public void close() throws Exception {
		trackers.forEach(ServiceTracker::close);
		framework.stop();
		framework.waitForStop(10000);
		executor.shutdownNow();
	}

	public BundleContext getBundleContext()

	{
		return context;
	}

	public <T> List<T> getServices(Class<T> class1) throws InvalidSyntaxException {
		BundleContext context = framework.getBundleContext();
		return context.getServiceReferences(class1, null).stream().map(context::getService)
				.collect(Collectors.toList());
	}

	public <T> T getService(Class<T> class1) throws Exception {
		List<T> services = getServices(class1);
		assertEquals(1, services.size());
		return services.get(0);
	}

	public <T> Promise<T> waitForService(Class<T> class1, long timeoutInMs) throws Exception {
		Deferred<T> deferred = new Deferred<>();
		executor.execute(() -> {
			ServiceTracker<T, T> tracker = new ServiceTracker<>(context, class1, null);
			tracker.open();
			try {
				T s = tracker.waitForService(timeoutInMs);
				if (s != null)
					deferred.resolve(s);
				else
					deferred.fail(new Exception("No service object " + class1));
			} catch (InterruptedException e) {
				deferred.fail(e);
			}
		});

		return deferred.getPromise();
	}

	public class BundleBuilder extends Builder {
		AtomicInteger			n					= new AtomicInteger();
		Map<String, Resource>	additionalResources	= new HashMap<>();

		BundleBuilder() {
			setBundleSymbolicName("test-" + n.incrementAndGet());
		}

		public BundleBuilder addResource(String path, URL url) {
			return addResource(path, new URLResource(url));
		}

		public BundleBuilder addResource(String path, Resource resource) {
			additionalResources.put(path, resource);
			return this;
		}

		public Bundle install() throws Exception {
			try {
				Jar jar = build();
				for (Entry<String, Resource> e : additionalResources.entrySet()) {
					jar.putResource(e.getKey(), e.getValue());
				}

				try (JarResource j = new JarResource(jar)) {

					return context.installBundle("generated " + jar.getBsn(), j.openInputStream());
				}
			} finally {
				close();
			}
		}

	}

	public BundleBuilder bundle() throws IOException {
		BundleBuilder bundleBuilder = new BundleBuilder();
		bundleBuilder.addClasspath(bin_test);
		return bundleBuilder;
	}

	public void addBundles(File bndrun) throws Exception {
		Run run = Run.createRun(getWorkspace(), bndrun);
		List<Bundle> bundles = new ArrayList<>();
		for (Container c : run.getRunbundles()) {
			assertNotNull(c.getError());
			Bundle bundle = context.installBundle(c.getFile().toURI().toString());
			bundles.add(bundle);
		}
		startAll(bundles);
	}

	public Workspace getWorkspace() throws Exception {
		if (workspace == null) {
			workspace = Workspace.getWorkspace(IO.work.getParentFile());
			// TODO fix the loading error
			// assertTrue(workspace.check());
			workspace.setOffline(true);
		}
		return workspace;
	}

	public Project getProject() throws Exception {
		project = getWorkspace().getProjectFromFile(IO.work);
		assertTrue(project.check());
		return project;
	}

	public void startAll(List<Bundle> bundles) throws BundleException {
		for (Bundle b : bundles) {
			b.start();
		}
	}

	public List<Bundle> addBundle(String spec) throws Exception {
		Parameters p = new Parameters(spec);
		List<Bundle> bundles = new ArrayList<>();
		for (Map.Entry<String, Attrs> e : p.entrySet()) {
			Container c = getProject().getBundle(e.getKey(), e.getValue().get("version"), Strategy.HIGHEST,
					e.getValue());
			assertNull(c.getError());
			Bundle bundle = context.installBundle(c.getFile().toURI().toString());
			bundles.add(bundle);
		}
		startAll(bundles);
		return bundles;
	}

}
