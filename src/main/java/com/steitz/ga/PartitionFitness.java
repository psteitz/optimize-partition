package com.steitz.ga;

import java.util.List;

/**
 * Interface for calculating the fitness of a partition represented as a list of
 * Integers.
 */
public interface PartitionFitness {
    double fitness(List<Integer> partition);
}
