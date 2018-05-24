package com.folkol.rx;

import com.google.gson.Gson;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.asynchttpclient.extras.rxjava.AsyncHttpObservable.*;

/**
 * Async RxJava client that branch HTTP requests for content ids -> content history -> content version.
 */
public class ContentClient {
    private static final String BASE_URL = "http://localhost:1234";

    private static final Gson GSON = new Gson();
    private static final Scheduler SCHEDULER = Schedulers.immediate();
    private static final AsyncHttpClient client = Dsl.asyncHttpClient();
    private static final AtomicInteger counter = new AtomicInteger();
    private static final AtomicInteger inFlight = new AtomicInteger();

    public static void main(String[] args) throws IOException {
        long begin = System.nanoTime();

        listContent()
            .flatMap(ContentClient::getContentHistory)
            .flatMap(ContentClient::getContent)
            .toBlocking()
            .subscribe(ContentClient::handleContent);

        long elapsed = System.nanoTime() - begin;
        double duration = elapsed / 1e9;
        int count = counter.get();
        System.out.printf("Fetched %d content in %.2f seconds (%.0f per second).%n",
                          count,
                          duration,
                          count / duration);

        client.close();
    }

    private static void handleContent(Content content) {
        // Do something with content

        counter.incrementAndGet();
        int requests = inFlight.decrementAndGet();
        int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        System.out.printf("requests: %-5d threads: %d%n", requests, threads);
    }

    private static Observable<String> listContent() {
        return get(BASE_URL + "/").flatMapIterable(ContentClient::parseList);
    }

    private static Observable<String> getContentHistory(String cid) {
        return get(BASE_URL + "/history/" + cid).flatMapIterable(ContentClient::parseList);
    }

    private static Observable<Content> getContent(String vcid) {
        inFlight.incrementAndGet();
        return get(BASE_URL + "/content/" + vcid).map(ContentClient::parseContent);
    }

    private static Content parseContent(Response response) {
        return GSON.fromJson(response.getResponseBody(), Content.class);
    }

    private static List<String> parseList(Response response) {
        return GSON.fromJson(response.getResponseBody(), ContentIds.class);
    }

    private static Observable<Response> get(String url) {
        return toObservable(() -> client.prepareGet(url)).subscribeOn(SCHEDULER);
    }
}
