package com.steitz.ga;

import java.io.PrintWriter;
import java.util.Arrays;
import java.io.File;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.util.MathArrays;

public class ClusterPartitionUtils {

    /**
     * Create a random clustered universe of dimension-dimensional vectors with the
     * given cluster size and number of clusters.
     * 
     * Starts by generating numClusters centroids randomly but with at least
     * centroidSeparation euclidean distance between any pair of centroids. These
     * are the first numClusters rows of the output array.
     * 
     * Then generates clusterSize - 1 random deviates around each of the centroids
     * and add these to the output array.
     * 
     * centroidSeparation should be at least 10 * sigma to ensure that centroids
     * plus deviates are the unique best solution to the clustering problem.
     * 
     * @param clusterSize        size of each cluster
     * @param numClusters        number of clusters
     * @param centroidSeparation minimum distance between centroids
     * @param sigma              standard deviation for random deviates
     * @param dimension          dimension of vectors in the universe
     * @return randomly generated universe clustered around numClusters randomly
     *         generated centroids
     */
    public static double[][] randomClusteredUniverse(int clusterSize, int numClusters, double centroidSeparation,
            double sigma,
            int dimension) {
        final RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

        final double[][] out = new double[numClusters * clusterSize][dimension];

        // Generate a centroid for each of the numClusters clusters.
        //
        // Start with the zero vector as initial cluster and then choose new centroids
        // by
        // randomly generating points and rejecting candidates that are within
        // centroidSeparation
        // of any of the previously defined centroid points.
        final double[][] centroids = new double[numClusters][dimension];
        final double sepSquared = centroidSeparation * centroidSeparation;
        for (int i = 0; i < numClusters; i++) {
            // Generate a random point in R^dimension - uniform distribution over
            // [-centroidSeparation^2, centroidSeparation^2]
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
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < i; j++) {
                assert (MathArrays.distance(centroids[i], centroids[j]) > centroidSeparation);
            }
        }

        // Fill the out array.
        // Start by adding centroids at the beginning of the array
        for (int i = 0; i < numClusters; i++) {
            out[i] = centroids[i];
        }

        int outIndex = numClusters; // Next index to write to out[]

        // Now generate clusterSize - 1 deviates around each centroid.
        // Generate componentDeviations as gaussian random variates with mean 0 and
        // standard deviation sigma.
        // add centroid + componentDeviations to out
        // This makes the structure of the universe:
        // centroids[0]
        // centroids[1]
        // ...
        // centroids[numClusters - 1]
        // deviate[0] around centroids[0]
        // deviate[1] around centroids[0]
        // ...
        // deviate[clusterSize - 2] around centroids[0]
        // deviate[0] around centroids[1]
        // ...
        // Get numclusters blocks of clusterSize - 1 deviates around each centroid in
        // order by centroid

        // loop over centroids, adding clusterSize - 1 deviates about each centroid
        for (int i = 0; i < numClusters; i++) {
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

    /**
     * Dump the universe to stdout
     * 
     * @param universe universe array
     */
    public static void dumpUniverse(double[][] universe) {
        for (int i = 0; i < 50; i++) {
            System.out.println(Arrays.toString(universe[i]));
        }
    }

    /**
     * Displays the closest centroids and the closest pair of deviates from
     * different blocks.
     * 
     * @param universe
     * @param numCentroids
     */
    public static void dumpUniverseDistanceMetrics(double[][] universe, int numCentroids) {
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

    /**
     * Write a universe to a CSV file.
     * Each row of the universe array is written to a line in the file.
     * 
     * @param universe universe array
     * @param filename filename to write to
     * @throws Exception if file cannot be created or written to
     */
    public static void universe2csv(double[][] universe, String filename) throws Exception {
        // Create filename if it doesn't exist
        try {
            File file = new File(filename);
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        try (PrintWriter pw = new PrintWriter(new File(filename))) {
            for (int i = 0; i < universe.length; i++) {
                for (int j = 0; j < universe[i].length; j++) {
                    pw.print(universe[i][j]);
                    if (j < universe[i].length - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
