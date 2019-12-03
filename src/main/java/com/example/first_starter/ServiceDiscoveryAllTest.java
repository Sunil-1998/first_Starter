package com.example.first_starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.MongoDataSource;

public class ServiceDiscoveryAllTest extends AbstractVerticle {

	@Override
	public void start() {
		// creating discovery service
		ServiceDiscovery discovery = ServiceDiscovery.create(vertx,
				new ServiceDiscoveryOptions().setAnnounceAddress("service-announce").setName("my-name"));

		// creating http endpoint record
		Record httpEndpointRecord = HttpEndpoint.createRecord("some-rest-api", "localhost", 8888, "/");

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
//						httpClient.getNow("/enum/threshold/getAll", responseHandler -> {
//							responseHandler.bodyHandler(bodyHandler -> {
//								System.out.println(bodyHandler);
//							});
//						});

				HttpRequest<Buffer> webClientBuffer = webClient.get("/");
				System.out.println("Buffer is :: " + webClientBuffer);
				webClientBuffer.send(asyncResult2 -> {
					if (asyncResult2.succeeded()) {
						System.out.println("AsString " + asyncResult2.result().bodyAsString());
					} else {
						asyncResult2.cause().printStackTrace();
					}
				});

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

		// Creating

		// create a new custom record
		Record eventBusRecord = new Record().setType("eventbus-service-proxy")
				.setLocation(new JsonObject().put("endpoint", "the-service-address")).setName("eventbus-service")
				.setMetadata(new JsonObject().put("some-label", "some-value"));

		// publish "eventbus-service" service
		discovery.publish(eventBusRecord, ar -> {
			if (ar.succeeded()) {
				System.out.println("\"" + eventBusRecord.getName() + "\" successfully published!");
				Record publishedRecord = ar.result();
			} else {
				// publication failed
			}
		});

		// unpublish "eventbus-service"
		discovery.unpublish(eventBusRecord.getRegistration(), ar -> {
			if (ar.succeeded()) {
				System.out.println("\"" + eventBusRecord.getName() + "\" successfully unpublished");
			} else {
				// cannot un-publish the service, may have already been removed, or the record
				// is not published
			}
		});

		JsonObject config = Vertx.currentContext().config();

		String uri = config.getString("mongo_uri");
		if (uri == null) {
			uri = "mongodb://localhost:27017";
		}
		String db = config.getString("mongo_db");
		if (db == null) {
			db = "test";
		}

		JsonObject mongoConfig = new JsonObject().put("connection_string", uri).put("db_name", db);

//		Record mongoRecord = MongoDataSource.createRecord("mongo-record",
//				new JsonObject().put("connection_string", "mongodb://localhost:27017"),
//				new JsonObject().put("database", "test"));

		Record mongoRecord = MongoDataSource.createRecord("some-mongo-db", mongoConfig, null);

		// publish "mongo-record" service
		discovery.publish(mongoRecord, ar -> {
			if (ar.succeeded()) {
				System.out.println("\"" + mongoRecord.getName() + "\" successfully published!");
				Record publishedRecord = ar.result();

//				System.out.println("Mongo Registration is ::" + publishedRecord.getRegistration());
			} else {
				// publication failed
				System.out.println("Mongo Record Publication Failed");
			}
		});

		// consuming "mongo-record" service
		discovery.getRecord(records -> records.getName().equals(mongoRecord.getName()), asyncResult -> {
			if (asyncResult.succeeded() && asyncResult.result() != null) {
				Record resultrecord = asyncResult.result();
				System.out.println("Got Mongo Record");

				System.out.println("Name is :" + resultrecord.getName());
				System.out.println("Location is :" + resultrecord.getLocation());
				System.out.println("metadata is :" + resultrecord.getMetadata());
				System.out.println("status is :" + resultrecord.getStatus());
				System.out.println("type is :" + resultrecord.getType());
				System.out.println("Registration is :" + resultrecord.getRegistration());
				System.out.println("class is :" + resultrecord.getClass());

				// Retrieve the service reference
				ServiceReference reference = discovery.getReference(resultrecord);
//				ServiceReference reference = discovery.getReferenceWithConfiguration(resultrecord,
//						new JsonObject().put("username", "admin").put("password", "admin"));
				MongoClient mongoClient = reference.get();

//				mongoClient.getCollections(resultHandler -> {
//					resultHandler.result().forEach(action -> {
//						System.out.println("action is ::" + action);
//					});
//				});

			} else {
				System.out.println("Mongo record Not Found");
			}

		});
		// unpublish "mongo-record" service
		discovery.unpublish(mongoRecord.getRegistration(), ar -> {
			if (ar.succeeded()) {
				System.out.println("\"" + mongoRecord.getName() + "\" successfully unpublished");
			} else {
				// cannot un-publish the service, may have already been removed, or the record
				// is not published
			}
		});

		discovery.close();
	}

}
