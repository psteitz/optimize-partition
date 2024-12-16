package com.steitz.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.apache.commons.math3.genetics.MutationPolicy;

public class PartitionChromosomeMutation implements MutationPolicy {

    /**
     * The fitness function used to evaluate the chromosome.
     */
    final PartitionFitness fitness;

    public PartitionChromosomeMutation(PartitionFitness fitness) {
        this.fitness = fitness;
    }

    /**
     * Mutate a PartitionChromosome by choosing a random index and replacing the
     * value at that index with a random value
     * in {0, ..., max} where max is the maximum value in the representation. Could
     * be no-op if the random value
     * happens to be the same as the current value.
     */
    @Override
    public Chromosome mutate(Chromosome original) throws InvalidRepresentationException {
        PartitionChromosome chromosome = (PartitionChromosome) original;
        // Make a copy of the representation
        List<Integer> representation = new ArrayList<>(chromosome.getRepresentation());
        // Get the maximum value in the representation
        int max = 0;
        for (Integer i : representation) {
            if (i > max) {
                max = i;
            }
        }
        // Choose a random index to mutate
        int index = (int) (Math.random() * representation.size());
        // Choose a random value in {0, ..., max} to replace the current value
        int value = (int) (Math.random() * (max + 1));
        representation.set(index, value);
        // Create a new partition so empty pieces can be removed
        final Partition partition = new Partition(representation);
        representation = partition.getRepresentation();
        return new PartitionChromosome(representation, fitness);
    }
}