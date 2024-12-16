
package com.steitz.ga;

import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;

public class PartitionChromosomeCrossover implements CrossoverPolicy {

    /** Fitness function */
    final PartitionFitness fitness;

    public PartitionChromosomeCrossover(PartitionFitness fitness) {
        this.fitness = fitness;
    }

    /**
     * Cross two PartitionChromosome instances by creating two children as follows:
     * The first child takes the value of the first parent for even indices,
     * and the second parent for odd indices. The second child does the opposite.
     */
    @Override
    public ChromosomePair crossover(Chromosome first, Chromosome second) {
        final List<Integer> list1 = ((PartitionChromosome) first).getRepresentation();
        final List<Integer> list2 = ((PartitionChromosome) second).getRepresentation();

        // If lists have different sizes, chromosomes can't be crossed. Throw IAE.
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("List sizes must be equal.");
        }

        // Find the smaller of the two partition lengths
        final int n = list1.size();

        // Cross the two partitions

        // Create arrays to hold the crossover chldren
        final int[] child1 = new int[n];
        final int[] child2 = new int[n];

        // Make the first chlld take the value of first parent for even indices, and the
        // second parent for odd indices.
        // Do the opposite for the second child
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                child1[i] = list1.get(i);
                child2[i] = list2.get(i);
            } else {
                child1[i] = list2.get(i);
                child2[i] = list1.get(i);
            }
        }
        Partition p1 = new Partition(child1);
        Partition p2 = new Partition(child2);

        return new ChromosomePair(new PartitionChromosome(p1.getRepresentation(), fitness),
                new PartitionChromosome(p2.getRepresentation(), fitness));
    }

}