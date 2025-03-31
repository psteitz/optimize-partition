package com.steitz.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.MathArrays;
import org.junit.jupiter.api.Test;

public class TestClusterPartitionChromosome {

    private static final int DIMENSION = 3;
    private static final int UNIVERSE_SIZE = 8;

    // Define universe as vertices of upper unit cube
    // {{0,0,0}, {1,1,1}, {1,0,1}, {1,1,0}, {0,1,0}, {0,0,1}, {0,1,1}, {1,0,0}};
    // 0 1 2 3 4 5 6 7
    private static final double[] ZZZ = { 0, 0, 0 };
    private static final double[] OOO = { 1, 1, 1 };
    private static final double[] OZO = { 1, 0, 1 };
    private static final double[] OOZ = { 1, 1, 0 };
    private static final double[] ZOZ = { 0, 1, 0 };
    private static final double[] ZZO = { 0, 0, 1 };
    private static final double[] ZOO = { 0, 1, 1 };
    private static final double[] OZZ = { 1, 0, 0 };

    private static double[][] universe = new double[UNIVERSE_SIZE][DIMENSION];
    /** Fill universe */
    static {
        universe[0] = ZZZ;
        universe[1] = OOO;
        universe[2] = OZO;
        universe[3] = OOZ;
        universe[4] = ZOZ;
        universe[5] = ZZO;
        universe[6] = ZOO;
        universe[7] = OZZ;
    }

    @Test
    public void testFitness() {
        // {{0,0,0}, {1,1,1}, {1,0,1}, {1,1,0}, {0,1,0}, {0,0,1}, {0,1,1}, {1,0,0}};
        // 0 1 2 3 4 5 6 7

        // Create partition with pieces {0,1,2}, {3,4,5}, {6,7}
        final Integer[] partition = { 0, 0, 0, 1, 1, 1, 2, 2 };
        // Applying the partion to the universe makes pieces {{0,0,0}, {1,1,1},
        // {1,0,1}},
        // {{1,1,0}, {0,1,0}, {0,0,1}} and {{0,1,1}, {1,0,0}}}.
        // Compute partition fitness by hand:
        // Go piece by piece, summing squares of pairwise euclidean distances.
        // {0,0,0}, {1,1,1} -> 1 + 1 + 1 = 3
        // {0,0,0}, {1,0,1} -> 1 + 0 + 1 = 2
        // {1,1,1}, {1,0,1} -> 0 + 1 + 0 = 1
        //
        // {1,1,0}, {0,1,0} -> 1 + 0 + 0 = 1
        // {1,1,0}, {0,0,1} -> 1 + 1 + 1 = 3
        // {0,1,0}, {0,0,1} -> 0 + 1 + 1 = 2
        //
        // {0,1,1}, {1,0,0} -> 1 + 1 + 1 = 3
        // Partition fitness should be neg sum of above = -15.
        //
        // Create ClusterPartitionChromosome from partition.
        final ClusterPartitionChromosome clusterPartitionChromosome = new ClusterPartitionChromosome(partition,
                DIMENSION, universe);
        // Check fitness
        assertEquals(-15, clusterPartitionChromosome.fitness(), 1e-12);
    }

}
