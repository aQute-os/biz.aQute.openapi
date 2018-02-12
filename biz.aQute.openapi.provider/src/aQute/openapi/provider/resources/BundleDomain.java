package aQute.openapi.provider.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.lib.strings.Strings;
import aQute.openapi.provider.resources.ResourceDomain.ResourceHolder;

@Component(service = BundleDomain.class)
class BundleDomain {
	public static final String			OPEN_API_RESOURCES	= "OpenAPI-Resources";
	final static Logger					logger				= LoggerFactory.getLogger(BundleDomain.class);

	@Reference
	ResourceDomain						server;
	private BundleTracker<BundleClause>	tracker;

	class BundleClause {
		final List<ResourceHolder>	holders;
		final String				hdr;
		final Bundle				bundle;

		BundleClause(String hdr, Bundle bundle) {
			this.bundle = bundle;
			this.holders = toHolders(hdr);
			this.hdr = hdr;
		}

		List<ResourceHolder> toHolders(String hdr) {
			List<ResourceHolder> holders = new ArrayList<>();

			for (Map.Entry<String,Attrs> e : OSGiHeader.parseHeader(hdr).entrySet()) {
				Attrs attrs = e.getValue();
				String[] uncached = toArray(attrs.get("uncached"));
				String[] compressed = toArray(attrs.get("compressed"));
				int maxAge = Integer.parseInt(attrs.getOrDefault("max-age", "600"));

				ResourceHolder holder = server.createHolder(bundle::getEntry, bundle.getLastModified(), maxAge,
						e.getKey(), compressed, uncached);

				holders.add(holder);
			}
			return holders;
		}

		private String[] toArray(String string) {
			if (string == null)
				return new String[0];

			List<String> split = Strings.split(string);
			return split.toArray(new String[split.size()]);
		}

		public void delete() {
			for (ResourceHolder rh : this.holders) {
				rh.delete();
			}
		}
	}

	@Activate
	void activate(BundleContext context) {
		tracker = new BundleTracker<BundleClause>(context, Bundle.ACTIVE, null) {
			@Override
			public BundleClause addingBundle(Bundle bundle, BundleEvent event) {
				String hdr = bundle.getHeaders().get(OPEN_API_RESOURCES);
				if (hdr == null)
					return null;

				logger.info("Detected bundle with static resources {} - {}", bundle, hdr);
				return new BundleClause(hdr, bundle);
			}

			@Override
			public void removedBundle(Bundle bundle, BundleEvent event, BundleClause bt) {
				bt.delete();
			}

		};
		tracker.open();
	}

	@Deactivate
	void deactivate() {
		tracker.close();
	}

}