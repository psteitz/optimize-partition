package com.steitz.ga;

import java.util.List;

public class HttpPartitionChromosome extends PartitionChromosome {

    /**
     * Create a new HttpPartitionChromosome with the given partition representation.
     * and command.
     * 
     * @param representation partition represented as a List of integers
     * @param url            url to submit get request to with representation in
     *                       partition querysting parameter
     */
    public HttpPartitionChromosome(List<Integer> representation, String url) {
        super(representation, new HttpPartitionFitness(url));
    }

    /***
     * Create a new CmdPartitionChromosome with the given partition representation.
     * 
     * @param representation partition represented as an array of Integers
     * @param url
     */
    public HttpPartitionChromosome(Integer[] representation, String url) {
        super(representation, new HttpPartitionFitness(url));
    }

}