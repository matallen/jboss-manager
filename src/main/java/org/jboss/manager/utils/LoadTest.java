package org.jboss.manager.utils;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.jayway.restassured.response.Response;

import net.java.quickcheck.Generator;

public class LoadTest {
	final String server = "http://localhost:24300";
	final String context = "simple-rest-app";
	final String uri = "rest/systemproperties/filter";

	public static void main(String[] s) {
		new LoadTest().run(3, 3000);
//		new LoadTest().request();
	}

	public void request() {
		Generator<String> generator = net.java.quickcheck.generator.PrimitiveGenerators.letterStrings(3, 10);
		String url = server + "/" + context + "/" + uri + "/" + generator.next();
		Response response = given().redirects().follow(true)
		// .body(body)
				.when().get(url);
		System.out.println("GET: " + url +" : "+response.statusLine() + " :: " + response.asString());
//		System.out.println(response.statusLine() + " :: " + response.asString());
	}
	
	public void run(int threads, int iterations) {
		int count=iterations;
		List<FutureTask<Void>> tasks = new ArrayList<FutureTask<Void>>();
		ExecutorService executor = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < iterations; i++) {
			tasks.add(new FutureTask<Void>(new Callable<Void>() {
				public Void call() throws Exception {
					new LoadTest().request();
					try {
						Generator<Long> generator = net.java.quickcheck.generator.PrimitiveGenerators.longs(100, 400);
						long wait = generator.next().longValue();
//						System.out.println("WAIT = " + wait);
						Thread.sleep(wait);
					} catch (Exception sink) {
						sink.printStackTrace();
					}
					return null;
				}
			}));
		}

		for (FutureTask<Void> t : tasks) {
			executor.submit(t);
		}

		for (FutureTask<Void> t : tasks) {
			while (!t.isDone()) {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException sink) {
				}
			}
			count=count-1;
			System.out.println("Task done ("+count+" request(s) remaining).");
		}
		executor.shutdown();
	}
}
