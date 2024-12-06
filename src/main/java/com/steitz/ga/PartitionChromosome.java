
package com.steitz.ga;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

public class PartitionChromosome extends AbstractListChromosome<Integer> {
    public PartitionChromosome(Integer[] representation) {
        super(representation);
        checkValidity(getRepresentation());
    }

    public PartitionChromosome(List<Integer> representation) {
        super(representation);
        checkValidity(representation);
    }

    @Override
    public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
        return new PartitionChromosome(chromosomeRepresentation);
    }

    /**
     * Return the integer list representation of the chromosome.
     */
    public List<Integer> getRepresentation() {
        return super.getRepresentation();
    }

    /**
     * Default implementation always returns 0.
     */
    @Override
    public double fitness() {
        return 0;
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

}
