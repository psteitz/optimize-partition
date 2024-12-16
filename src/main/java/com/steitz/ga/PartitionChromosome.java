
package com.steitz.ga;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

public class PartitionChromosome extends AbstractListChromosome<Integer> {

    private final PartitionFitness fitness;

    public PartitionChromosome(Integer[] representation, PartitionFitness fitness) {
        super(representation);
        checkValidity(getRepresentation());
        this.fitness = fitness;
    }

    public PartitionChromosome(List<Integer> representation, PartitionFitness fitness) {
        super(representation);
        checkValidity(representation);
        this.fitness = fitness;
    }

    @Override
    public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
        return new PartitionChromosome(chromosomeRepresentation, fitness);
    }

    /**
     * Return the integer list representation of the chromosome.
     */
    public List<Integer> getRepresentation() {
        return super.getRepresentation();
    }

    /**
     * Compute the fitness of the chromosome.
     */
    @Override
    public double fitness() {
        return fitness.fitness(getRepresentation());
    }

    /**
     * Check the validity of the chromosome representation.
     * Values must be {0, ... , n} for some n.
     */
    @Override
    protected void checkValidity(List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
        // Make sure they are all non-negative and capture the maximum value.
        int max = 0;
        for (Integer i : chromosomeRepresentation) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid representation: " + i);
            }
            if (i > max) {
                max = i;
            }
        }
        // Now verify that each value from 0 to max is present.
        for (int i = 0; i < max; i++) {
            if (!chromosomeRepresentation.contains(i)) {
                throw new IllegalArgumentException("Missing partitioon: " + i);
            }
        }
    }

    /**
     * @return the fitness function
     */
    public PartitionFitness getFitnessFunction() {
        return fitness;
    }

}
