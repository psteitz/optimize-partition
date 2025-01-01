package com.steitz.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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
    //{{0,0,0}, {1,1,1}, {1,0,1}, {1,1,0}, {0,1,0}, {0,0,1}, {0,1,1}, {1,0,0}};
    //  0        1        2        3        4        5        6        7
    private static final double[] ZZZ = {0,0,0};
    private static final double[] OOO = {1,1,1};
    private static final double[] OZO = {1,0,1};
    private static final double[] OOZ = {1,1,0};
    private static final double[] ZOZ = {0,1,0};
    private static final double[] ZZO = {0,0,1};
    private static final double[] ZOO = {0,1,1};
    private static final double[] OZZ = {1,0,0};

    private static double[][] universe = new double[UNIVERSE_SIZE][DIMENSION];
    /** Fill universe  */
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
        //{{0,0,0}, {1,1,1}, {1,0,1}, {1,1,0}, {0,1,0}, {0,0,1}, {0,1,1}, {1,0,0}};
        //    0        1        2        3        4        5        6        7

        // Create partition with pieces {0,1,2}, {3,4,5}, {6,7}
        final Integer[] partition = {0,0,0,1,1,1,2,2};
        // Applying the partion to the universe makes pieces {{0,0,0}, {1,1,1}, {1,0,1}},
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
        final ClusterPartitionChromosome clusterPartitionChromosome = new ClusterPartitionChromosome(partition, DIMENSION, universe);
        // Check fitness
        assertEquals(-15,clusterPartitionChromosome.fitness(), 1e-12);                                                                      
    }

    @Test
    public void testClusterPartitionChromosomeClusteredUniverse() {
        System.out.println("Starting testClusterPartitionChromosomeClusteredUniverse");

        // Set the tournament arity - the number of chromosomes to include in each
        // population
        final int TOURNAMENT_ARITY = 1000;

        // Set the number of generations to run the genetic algorithm
        final int NUM_GENERATIONS = 100;

        final double[][] universe = clusteredUniverse(10,5,10,.1,DIMENSION);

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
        // Neighboring clusters are at least 10 away, so one wrong placement will cause this to fail.
        System.out.println("Best fitness: " + bestFinal.fitness());
        assertTrue(bestFinal.fitness() > -27);
        System.out.println(bestFinal);
    }

    private Population getInitialPopulation(double[][] universe) {
        final int populationSize = 1000;
        final int universeSize = 50;
        final int numClusters = 5;

        final ClusterPartitionChromosome[] chromosomes = new ClusterPartitionChromosome[populationSize];
         for (int i = 0; i < populationSize; i++) {
            final Partition randomPartition = Partition.randomPartition(universeSize,numClusters);
            final List<Integer> representation = randomPartition.getRepresentation();
            chromosomes[i] = new ClusterPartitionChromosome(representation,3, universe);
        }
        final Population out = new ElitisticListPopulation(populationSize, 0.1);
        for (int i = 0; i < populationSize; i++) {
            out.addChromosome(chromosomes[i]);
        }
        return out;
    }


    /**
     * Create a clustered universe of dimension-dimensional vectors with the given cluster size and number of clusters.
     * 
     * Starts by generating numClusters centroids randomly but with at least centroidSeparation euclidean distance between 
     * any pair of centroids. These are the first numClusters rows of the output array.
     * 
     * Then generates clusterSize random deviates around each of the centroids and adds these to the output array.
     * 
     * @param clusterSize size of each cluster
     * @param numClusters number of clusters
     * @param centroidSeparation minimum distance between centroids
     * @param sigma standard deviation for random deviates
     * @param dimension dimension of vectors in the universe
     * @return randomly generated universe clustered around numClusters randomly generated centroids
     */
    private double[][] clusteredUniverse(int clusterSize, int numClusters, double centroidSeparation, double sigma, int dimension) {
        final RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

        final double[][] out = new double[numClusters * clusterSize][dimension];

        // Generate a centroid for each of the numClusters clusters.
        //
        // Start with the zero vector as initial cluster and then choose new centroids by
        // randomly generating points and rejecting candidates that are within centroidSeparation
        // of any of the previously defined centroid points.
        final double[][] centroids = new double[numClusters][dimension];
        final double sepSquared = centroidSeparation * centroidSeparation;
        for (int i = 0; i < numClusters; i++) {
            // Generate a random point in R^dimension - uniform distribution over [-centroidSeparation^2, centroidSeparation^2]
            // for each component
            boolean done = false;
            while (!done) {
                // Fill candidate new centroid with random values
                for (int j = 0; j < dimension; j++) {
                        centroids[i][j] = randomDataGenerator.nextUniform(-sepSquared, sepSquared);
                }
                // Compare centroids[i] with the other ones defined so far
                // Reject if it is too close to any of them
                done = true;
                for (int j = 0; j < i; j++) {
                    if (MathArrays.distance(centroids[i], centroids[j]) < centroidSeparation) {
                        done = false;
                    }
                }
            }
        }

        // Verify that the centroids are separated
        for(int i = 0; i < numClusters; i++) {
            for (int j = 0; j < i; j++) {
                assert(MathArrays.distance(centroids[i], centroids[j]) > centroidSeparation);
            }
        }

        // Fill the out array.
        // Start with add centroids at the beginning of the array
        for (int i = 0; i < numClusters; i++) {
            out[i] = centroids[i];
        }

        int outIndex = numClusters; // Next index to write to out[]

        // Now generate clusterSize - 1 elements around each centroid.
        // Generate componentDeviations as gaussian random variates with mean 0 and standard deviation sigma
        // add centroid + componentDeviations to out

        // loop over centroids, adding clusterSize - 1 deviates about each centroid
        for (int i =0; i < numClusters; i++) {
            for (int j = 0; j < clusterSize - 1; j++) {
                // Create deviate vector to add to centroid
                final double[] deviate = new double[dimension];
                for (int k = 0; k < dimension; k++) {
                    deviate[k] = randomDataGenerator.nextGaussian(0, sigma) + centroids[i][k];
                }
                out[outIndex++] = deviate;
            }
        }
        return out;
    }

    @Test 
    public void testClusteredUniverse() {
        final double[][] universe = clusteredUniverse(10,5,10,1,3);
        for (int i = 0; i < 50; i++) {
            System.out.println(Arrays.toString(universe[i]));
        }
    }
}