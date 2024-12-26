package com.steitz.ga;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.google.common.util.concurrent.AtomicDouble;

public class HttpPartitionFitness implements PartitionFitness {
    /**
     * Maximum number of history entries.
     */
    protected static final int HISTORY_SIZE = 10;

    /**
     * History of fitness <args, fitness> pairs where fitness is what is rturned by
     * https://url?partition=args
     * 
     * args is a comma separated list of integers surrounded by square brackets.
     */
    protected final Map<String, Double> history = new HashMap<String, Double>();

    // URL to send GET request to
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
     * of integers, surrounded by square brackets. For example, "[0, 1, 2]".
     * 
     * The body of the response is parsed as a double and returned.
     */
    @Override
    public double fitness(List<Integer> partition) {
        // Submit a "GET" request to url with the partition as a query string
        // parameter. URL encode the request. Parse the response body as a double and
        // return it.

        // Construct the url qith the partition as a query string parameter
        final String baseUrl = url + "?partition=";

        // Encode the partition list
        String encodedPartition = null;
        try {
            encodedPartition = URLEncoder.encode(partition.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // encoding failed - stack trace, throw RTE
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        final AtomicDouble result = new AtomicDouble();

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder
                    .get(baseUrl + encodedPartition).build();
            System.out.println("Partition fitness request sent to " + httpGet.getUri());
            System.out.println("Method: " + httpGet.getMethod());
            System.out.println("Encoded partition: " + encodedPartition);
            httpclient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                System.out.println(response.getHeaders());
                System.out.println(response.getEntity());
                final InputStream contentStream = response.getEntity().getContent();
                // Serialize the response content into a string
                final StringBuilder sb = new StringBuilder();
                int c;
                while ((c = contentStream.read()) != -1) {
                    sb.append((char) c);
                }
                contentStream.close();
                System.out.println("Response content: " + Double.parseDouble(sb.toString()));
                // Parse the response content as a double and set the result
                result.set(Double.parseDouble(sb.toString()));
                return response;
            });
        } catch (Exception e) {
            System.out.println(baseUrl);
            System.out.println(encodedPartition);
            throw new RuntimeException(e);
        }
        return result.get();
    }
}