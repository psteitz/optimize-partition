
package com.steitz.ga;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

public class PartitionChromosome extends AbstractListChromosome<Integer> {

    private final PartitionFitness fitnessFunction;

    private double cachedFitness = Double.NaN;

    private ReentrantReadWriteLock fitnessLock = new ReentrantReadWriteLock();

    public PartitionChromosome(Integer[] representation, PartitionFitness fitness) {
        super(representation);
        checkValidity(getRepresentation());
        this.fitnessFunction = fitness;
    }

    public PartitionChromosome(List<Integer> representation, PartitionFitness fitness) {
        super(representation);
        checkValidity(representation);
        this.fitnessFunction = fitness;
    }

    @Override
    public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
        return new PartitionChromosome(chromosomeRepresentation, fitnessFunction);
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
        // Use a read lock to check if the fitness is already computed.
        double result = Double.NaN;
        fitnessLock.readLock().lock();
        if (!Double.isNaN(cachedFitness)) {
            result = cachedFitness;
            fitnessLock.readLock().unlock();
        } else {
            // Upgrade to a write lock to compute the fitness.
            fitnessLock.readLock().unlock();
            fitnessLock.writeLock().lock();
            try {
                if (Double.isNaN(cachedFitness)) {
                    cachedFitness = fitnessFunction.fitness(getRepresentation());
                    // System.out.println("representation " + getRepresentation());
                    // System.out.println("computed fitness " + cachedFitness);
                }
                result = cachedFitness;
            } finally {
                fitnessLock.writeLock().unlock();
            }
        }
        // System.out.println("Returning fitness: " + result);
        return result;
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
        return fitnessFunction;
    }

}
