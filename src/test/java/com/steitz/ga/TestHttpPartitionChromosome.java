
package com.steitz.ga;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

/**
 * Mock an http server that computes partition fitness and returns the value in
 * http response body.
 * 
 * Sever expects GET requests with a query string parameter "partition" that is
 * a url-encoded comma separated list of integers surronuded by square brackets.
 * 
 * Different tests set it up to do different fitness computations.
 */

// WireMockTest annotation starts and stops the server for each test
@WireMockTest
public class TestHttpPartitionChromosome {

    /** Test partitions */
    final static String[] TEST_PARTITIONS = { "[0,1,2,3,4,5,6,7,8,9]", "[0,1,0,2,0,3,0,4]", "[0,1,0,2,0,3,0,4,0,5]",
            "[0,1,0,2,0,3,4,6,5,7,8,9]" };

    private String getSumString(String partition) {
        return Double.toString(getSum(partition));
    }

    private int getSum(String partition) {
        // Parse the partition string
        String[] parts = partition.substring(1, partition.length() - 1).split(",");
        int sum = 0;
        for (String part : parts) {
            sum += Integer.parseInt(part);
        }
        return sum;
    }

    private List<Integer> getPartition(String partition) {
        // Parse the partition string
        String[] parts = partition.substring(1, partition.length() - 1).split(",");
        List<Integer> out = new ArrayList<Integer>();
        for (String part : parts) {
            out.add(Integer.parseInt(part));
        }
        return out;
    }

    @Test
    void testGetSum(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {

        stubFor(get("/static-dsl").willReturn(ok()));
        final WireMock wireMock = wmRuntimeInfo.getWireMock();

        // Hard wire a static response for each test partition.
        // Recognize urls of the form /instance-dsl/sum?partition=encodedPartition
        // where encodedPartition is the bracketed list representation of one of the
        // test partitions,
        // url-encoded.
        //
        // Return the sum of the partition as the response body.
        for (String partition : TEST_PARTITIONS) {
            final String encodedPartition = URLEncoder.encode(getPartition(partition).toString(), "UTF-8");
            System.out.println("encodedPartition: " + encodedPartition);
            System.out.println("sum " + getSumString(partition));
            wireMock.register(get("/instance-dsl/sum?partition=" + encodedPartition)
                    .willReturn(aResponse().withBody(getSumString(partition))));
        }
        // Get the port of the wiremock server
        final int port = wmRuntimeInfo.getHttpPort();

        // Create a HttpPartitionChromosome with the url and port of the wiremock server
        final HttpPartitionChromosome chromosome = new HttpPartitionChromosome(getPartition(TEST_PARTITIONS[0]),
                "http://localhost:" + port + "/instance-dsl/sum");

        // Fitness should return the sum of the partition
        // [0,1,2,3,4,5,6,7,8,9]
        assert (chromosome.fitness() == 45.0);

        // Try another one
        final HttpPartitionChromosome chromosome2 = new HttpPartitionChromosome(getPartition(TEST_PARTITIONS[1]),
                "http://localhost:" + port + "/instance-dsl/sum");
        // [0,1,0,2,0,3,0,4] -> 10
        assert (chromosome2.fitness() == 10.0);
    }
}