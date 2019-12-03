package com.example.first_starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public class ServiceDiscoveryAllTest extends AbstractVerticle {

	@Override
	public void start() {
		// creating discovery service
		ServiceDiscovery discovery = ServiceDiscovery.create(vertx,
				new ServiceDiscoveryOptions().setAnnounceAddress("service-announce").setName("my-name"));
	}

}
