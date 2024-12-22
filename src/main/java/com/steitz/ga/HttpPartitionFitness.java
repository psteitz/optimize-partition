package com.steitz.ga;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

public class HttpPartitionFitness implements PartitionFitness {
    /**
     * Maximum number of history entries.
     */
    protected static final int HISTORY_SIZE = 10;

    /**
     * History of fitness <args, fitness> pairs where fitness is what is rturned by
     * command "args"
     */
    protected final Map<String, Double> history = new HashMap<String, Double>();

    // Command to execute
    private final String url;

    /**
     * Create a new CmdPartitionFitness with the given command.
     */
    public HttpPartitionFitness(String url) {
        this.url = url;
    }

    /**
     * Compute fitness by sending a "GET" request to url with the partition as
     * query string parameter. partition parameter is sent as a comma separated list
     * of
     * integers, surrounded by square brackets. For example, "[0, 1, 2]".
     */
    @Override
    public double fitness(List<Integer> partition) {
        // Use Commons HttpClient 5.4.1 to submit a "GET" request to the given URL with
        // the partition as a query string parameter.
        // Parse the response body as a double and return it.
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url + "?partition=" + partition.toString()).build();
            httpclient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final HttpEntity entity1 = response.getEntity();
                // Serialize the response content into a string

                final StringBuilder sb = new StringBuilder();
                while (entity1.getContentLength() > 0) {
                    sb.append(entity1.getContent());
                }
                return Double.parseDouble(sb.toString());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0; // unreachable

    }
}