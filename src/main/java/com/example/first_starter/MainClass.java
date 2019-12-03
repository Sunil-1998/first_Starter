package com.example.first_starter;

import io.vertx.core.Vertx;

public class MainClass {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(MainVerticle.class.getName());
		vertx.deployVerticle(ServiceDiscoveryVerticle.class.getName());
		//MongoClientTest
		vertx.deployVerticle(MongoClientCustom.class.getName());
		//ServiceDiscoveryAllTest
		vertx.deployVerticle(ServiceDiscoveryAll.class.getName());
	}

}
