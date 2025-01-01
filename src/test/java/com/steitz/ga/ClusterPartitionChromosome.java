package com.steitz.ga;

import java.util.List;

/**
 * A PartitionChromosome that computes fitness by summing the within-cluster
 * pairwise euclidean distances between points in the cluster, where clusters
 * are partition pieces.
 */

public class ClusterPartitionChromosome extends PartitionChromosome {

    /**
     * Create a ClusterPartitionChromosome from Integer array representation of a partition
     * of universe of dimension-dimensional vectors.
     * 
     * @param representation entries in representation are partition piece numbers for the corresponding
     *                       entries (row numbers) in universe
     * @param dimension number of elements in each row of universe = dimension of the vectors in the universe.
     * @param universe universe of dimension-dimensional vectors. Rows are universe elements, cols are their components
     * 
     */
    public ClusterPartitionChromosome(Integer[] representation, int dimension, double[][]  universe) {
        super(representation, new ClusterPartitionFitness(dimension, universe));
        checkValidity(getRepresentation());
    }

    public ClusterPartitionChromosome(List<Integer> representation, int dimension, double[][] universe) {
        super(representation, new ClusterPartitionFitness(dimension, universe));
        checkValidity(representation);
    }

}