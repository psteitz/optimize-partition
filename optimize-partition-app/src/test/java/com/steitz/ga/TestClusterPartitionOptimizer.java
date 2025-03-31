package com.steitz.ga;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Before;
import org.junit.Test;

public class TestClusterPartitionOptimizer {

    private static String TEST_FILE_NAME = "universe.csv";

    private static ReentrantLock fileLock = new ReentrantLock();

    @GuardedBy("fileLock")
    private static AtomicBoolean setupDone = new AtomicBoolean(false);

    /**
     * Setup
     * Create or overwrite TEST_FILE_NAME with random universe. Do this just once.
     */
    @Before
    public void setUp() {
        final double[][] universe = ClusterPartitionUtils.randomClusteredUniverse(10, 5, 10.0, 0.1, 3);
        final String home = System.getProperty("user.home");
        try {
            fileLock.lock();
            System.out.println("Creating test universe file: " + home
                    + "/optimize-partition/optimize-partition-app/src/test/resources/" + TEST_FILE_NAME);
            ClusterPartitionUtils.universe2csv(universe,
                    home + "/optimize-partition/optimize-partition-app/src/test/resources/" + TEST_FILE_NAME);
            setupDone.set(true);
            System.out.println("Test universe file created.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            fileLock.unlock();
        }
    }

    /**
     * Test cluster gaussian deviates around 5 centroids.
     */
    @Test
    public void testClusterPartitionChromosomeClusteredUniverse() {
        System.out.println("Starting testClusterPartitionChromosomeClusteredUniverse");

        // Set the tournament arity - the number of chromosomes to include in each
        // population
        final int TOURNAMENT_ARITY = 100;

        // Set the number of generations to run the genetic algorithm
        final int NUM_GENERATIONS = 100;

        final int DIMENSION = 5;

        // universe is 5 random centroids at least 10 units apart followed by
        // consecutive blocks of clustersize
        // deviates around the five centroids in sequence. So universe[5], ...,
        // universe[14] are deviates
        // around universe[0], etc
        final double[][] universe = ClusterPartitionUtils.randomClusteredUniverse(10, 5, 10, .1, DIMENSION);

        ClusterPartitionUtils.dumpUniverseDistanceMetrics(universe, 5);

        final ClusterPartitionFitness fitness = new ClusterPartitionFitness(DIMENSION, universe);

        // initialize a new genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new PartitionChromosomeCrossover(fitness),
                1,
                new PartitionChromosomeMutation(fitness),
                0.10,
                new TournamentSelection(TOURNAMENT_ARITY));

        // initial population
        System.out.println("Generating initial population");
        Population initial = getInitialPopulation(universe);
        System.out.println("Initial population generated");

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();
        // If all elements are in the right clusters, distances between
        // any two should be at most 2sigma in each component -> .2 * 3 = .6.
        // Cluster size is 10, so (10 choose 2) * .6 = 27 should never be exceeded.
        // Neighboring clusters are at least 10 away, so one wrong placement will cause
        // this to fail.
        System.out.println("Best fitness: " + bestFinal.fitness());
        if (bestFinal.fitness() < -27) {
            System.out.println("Failing.  Solution fitness too low.");
            System.out.println("Universe");
            ClusterPartitionUtils.dumpUniverse(universe);
            System.out.println("Best partition found");
            System.out.println(bestFinal);
            fail();
        }
        System.out.println("Best Partition:");
        System.out.println(bestFinal);

        // Verify that bestFinal has first five values different - separates the
        // centroids
        final List<Integer> bestList = ((PartitionChromosome) bestFinal).getRepresentation();
        final HashSet<Integer> centroids = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            centroids.add(bestList.get(i));
        }
        assertEquals(centroids.size(), 5);

        // FIXME: change to constants. These are clusteredUniverse parms.
        final int numClusters = 5;
        final int clusterSize = 10;

        // Following the centroids in universe[] are blocks of clusterSize - 1 deviates
        // around each centroid in sequence.
        // So after the 5th element, bestList should be sequences of identical integer
        // values of length clusterSize.
        // Something like this:
        // 4, 2, 0, 1, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0,
        // 0, 0, 0, 0, 0, 0, ...
        // Best fitness: -11.559283605643632
        int offset = 5; // offset into bestList
        for (int i = 0; i < numClusters; i++) {
            final HashSet<Integer> values = new HashSet<>();
            for (int j = 0; j < clusterSize - 1; j++) {
                final int nextValue = bestList.get(offset + (i * (clusterSize - 1)) + j);
                values.add(nextValue);
            }
            assertEquals(1, values.size());
        }

        System.out.println(bestFinal);
    }

    private Population getInitialPopulation(double[][] universe) {
        final int populationSize = 1000;
        final int universeSize = 50;
        final int numClusters = 5;

        final ClusterPartitionChromosome[] chromosomes = new ClusterPartitionChromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            final Partition randomPartition = Partition.randomPartition(universeSize, numClusters);
            final List<Integer> representation = randomPartition.getRepresentation();
            chromosomes[i] = new ClusterPartitionChromosome(representation, 3, universe);
        }
        final Population out = new ElitisticListPopulation(populationSize, 0.1);
        for (int i = 0; i < populationSize; i++) {
            out.addChromosome(chromosomes[i]);
        }
        return out;
    }

    /**
     * Displays the closest centroids and the closest pair of deviates from
     * different blocks.
     * 
     * @param universe
     * @param numCentroids
     */
    private void dumpUniverseDistanceMetrics(double[][] universe, int numCentroids) {
        // First find smallest distance between centroids
        double closest = Double.MAX_VALUE;
        int bestI = 0;
        int bestJ = 0;
        for (int i = 0; i < numCentroids; i++) {
            for (int j = 0; j < i; j++) {
                double curDist = MathArrays.distance(universe[i], universe[j]);
                if (curDist < closest) {
                    closest = curDist;
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        System.out.println("Closest centroids are");
        System.out.println(Arrays.toString(universe[bestI]));
        System.out.println(Arrays.toString(universe[bestJ]));
        System.out.println("These two are " + closest + " units  apart.");

        // Now find the smallest inter-cluster distance
        // Assume universe has centroids first, followed by blocks of deviates
        int blockSize = (universe.length - numCentroids) / numCentroids;
        System.out.println("Deviate blocksize: " + blockSize);
        int blockStart = numCentroids;
        closest = Double.MAX_VALUE;
        for (int i = 0; i < numCentroids; i++) {
            for (int j = 0; j < blockSize; j++) {
                double[] ref = universe[blockStart + j];
                // Compare to deviates in other blocks below it
                for (int k = numCentroids; k < blockStart; k++) {
                    double curDist = MathArrays.distance(ref, universe[k]);
                    if (curDist < closest) {
                        closest = curDist;
                        bestI = blockStart + j;
                        bestJ = k;
                    }
                }
            }
            blockStart += blockSize;
        }
        System.out.println("Closest deviates are");
        System.out.println(Arrays.toString(universe[bestI]));
        System.out.println(Arrays.toString(universe[bestJ]));
        System.out.println("These two are " + closest + " units  apart.");
    }

}