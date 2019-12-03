package com.example.first_starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoClientTest extends AbstractVerticle {

	/**
	 * public class Demo { String name; String value;
	 * 
	 * Demo(String name, String value) { this.value = value; this.name = name; }
	 * 
	 * public JsonObject getJsonObject() { return new JsonObject().put("name",
	 * this.name).put("value", this.value); } }
	 */

	@Override
	public void start() {
		System.out.println("Mongo Test started");

		JsonObject mongoconfig = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name",
				"GraphQlDemo");

		MongoClient mongoClient = MongoClient.createShared(vertx, mongoconfig);

//		mongoClient.getCollections(resultHandler -> {
//			if (resultHandler.succeeded() && resultHandler.result() != null) {
//				resultHandler.result().forEach(result -> {
//					System.out.println("Result is ::" + result);
//					mongoClient.findBatch(result, new JsonObject()).handler(handler -> {
//						System.out.println("handler is ::" + handler);
//						handler.forEach(action -> {
//							System.out.println("action is ::" + action);
//						});
//						System.out.println();
//					});
//				});
//			}
//		});

		JsonObject product1 = new JsonObject().put("itemId", "12345").put("name", "Cooler").put("price", "100.0");
		mongoClient.save("products", product1, id -> {
			System.out.println("Inserted id: " + id.result());

			mongoClient.find("products", new JsonObject().put("itemId", "12345"), res -> {
				System.out.println("Name is " + res.result().get(0).getString("name"));

				System.out.println("res.result() is :: " + res.result());

				if (res.succeeded() && res.result() != null) {
					res.result().forEach(result -> {
						System.out.println("Result is ::" + result);
					});
				}

				mongoClient.removeDocument("products", new JsonObject().put("itemId", "12345"), rs -> {
					if (rs.succeeded()) {
						System.out.println("Product removed ");
					}
				});
			});
		});

	}
}
