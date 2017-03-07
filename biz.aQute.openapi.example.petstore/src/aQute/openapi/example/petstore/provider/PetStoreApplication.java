package aQute.openapi.example.petstore.provider;

import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireBootstrapWebResource
public class PetStoreApplication {}
