/**
 * Fitness function that 
 */

package com.steitz.ga;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A chromosome for the partition problem where the fitness is the sum of the
 * max value of the elements in each piece.
 */

public class MaxValuePartitionChromosome extends PartitionChromosome {
    private static final int[] universe = new int[100];

    /** Setup popluation to have value 10 for first 5 elements, 0 for others. */
    static {
        for (int i = 0; i < universe.length; i++) {
            if (i < 5) {
                universe[i] = 10;
            } else {
                universe[i] = 0;
            }
        }
    }

    public MaxValuePartitionChromosome(Integer[] representation) {
        super(representation);
        checkValidity(getRepresentation());
    }

    public MaxValuePartitionChromosome(List<Integer> representation) {
        super(representation);
        checkValidity(representation);
    }

    /**
     * Get the fitness of a partition.
     * 
     * The fitness is the sum of the max value of the elements in each piece.
     * 
     * @param partition the partition
     * 
     * @return the fitness of the partition
     */
    @Override
    public double fitness() {

        final Partition partition = new Partition(getRepresentation());
        // Get the subsets of the partition
        final List<Set<Integer>> subsets = partition.asSets().values().stream().collect(Collectors.toList());

        // Compute the sum of the max value for universe[i] for each i in the partition
        int sum = 0;
        // Loop over the subsets
        for (Set<Integer> subset : subsets) {
            // Get the max value in the subset
            int max = subset.stream().mapToInt(i -> universe[i]).max().getAsInt();
            // Add the max value to the sum
            sum += max;
        }
        return sum;
    }
}