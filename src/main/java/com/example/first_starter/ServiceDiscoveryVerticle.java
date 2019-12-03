package com.example.first_starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class ServiceDiscoveryVerticle extends AbstractVerticle {

	@Override
	public void start() {

		// creating discovery service
		ServiceDiscovery discovery = ServiceDiscovery.create(vertx,
				new ServiceDiscoveryOptions().setAnnounceAddress("service-announce").setName("my-name"));

		// creating http endpoint record
		Record httpEndpointRecord = HttpEndpoint.createRecord("some-rest-api", "localhost", 8888,
				"/");

		// publishing http endpoint record
		discovery.publish(httpEndpointRecord, asyncResult -> {
			if (asyncResult.succeeded()) {
				System.out.println("Http record published sucessfully");
			} else {
				System.out.println("Http record can not be published");
			}
		});

		// consuming http endpoing with http client

		discovery.getRecord(filter -> filter.getName().equals(httpEndpointRecord.getName()), asyncResult -> {
			if (asyncResult.succeeded() && asyncResult.result() != null) {
				System.out.println("Got Record");
				ServiceReference httpReference = discovery.getReference(httpEndpointRecord);
				HttpClient httpClient = httpReference.get();

				WebClient webClient = WebClient.wrap(httpClient);

				System.out.println("Consuming \"" + httpEndpointRecord.getName() + "\"");
//				httpClient.getNow("/enum/threshold/getAll", responseHandler -> {
//					responseHandler.bodyHandler(bodyHandler -> {
//						System.out.println(bodyHandler);
//					});
//				});

				HttpRequest<Buffer> webClientBuffer = webClient.get("/");
				System.out.println("Buffer is :: " + webClientBuffer);
				webClientBuffer.send(asyncResult2 -> {
					if (asyncResult2.succeeded()) {
						System.out.println("AsString " + asyncResult2.result().bodyAsString());
					} else {
						asyncResult2.cause().printStackTrace();
					}
				});

//				webClientBuffer.send(asyncResult3 -> {
//					System.out.println("JsonObject " + asyncResult3.result().bodyAsJsonObject());
//				});

			} else {
				System.out.println("Didn't got Record");
			}
		});

		// unpublishing http endpoint record

		discovery.unpublish(httpEndpointRecord.getRegistration(), asyncResult -> {
			if (asyncResult.succeeded()) {
				System.out.println("Http record unpublished sucessfully");
			} else {
				System.out.println("Http record can not be unpublished");
			}
		});

		discovery.close();
	}

}
