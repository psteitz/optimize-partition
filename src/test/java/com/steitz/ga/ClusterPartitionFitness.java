package com.steitz.ga;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.util.MathArrays;

/**
 * Compute fitness of the partition by negative sum of squared pairwise euclidean
 * distances among elements of partition pieces.
 * Universe is set of dimension-dimensional vectors.  
 */

public class ClusterPartitionFitness implements PartitionFitness {
    
    /** dimension of the space that the points to be clustered come from. */
    private final int dimension;

    /** 
     * universe is a two-dimensional array of doubles.
     * 
     * Rows are elements of the universe represented as dimension-dimensional arrays.
     */
    private final double[][] universe;

    public ClusterPartitionFitness(int dimension, double[][] universe) {
        this.dimension = dimension;
        this.universe = universe;
    }

    /**
     * Compute the fitness of the partition by negative summing the within-cluster
     * distances between points over all clusters. 
     * 
     * Clusters are defined by partition pieces.
     */
    @Override
    public double fitness(List<Integer> partition) {
        
        // Pieces is a map keyed on partition piece number with value a set of indexes
        // of elements in the piece.
        final Map<Integer,Set<Integer>> pieces = getPieces(partition);

        // Sum of squared pairwise distances within partition piece
        double ssto = 0;

        // For each entry of pieces, compute sum of squared distances
        // between universe elements pointed to by the integers in the values set.
        for (Entry<Integer,Set<Integer>> entry : pieces.entrySet()) {
            // Load universe vectors included in this piece to vectors array
            final int pieceSize = entry.getValue().size();
            final double[][] vectors = new double[pieceSize][dimension];
            int ct = 0;
            for (Integer index : entry.getValue()) {
                vectors[ct++] = universe[index];
            }

            // Loop over vectors to compute sum of pairwise squared distances
            for (int i = 0; i < pieceSize; i++) {
                for (int j = 0; j < i; j++) {
                    final double dist = MathArrays.distance(vectors[i], vectors[j]);
                    ssto += dist*dist;
                }
            }
        }

        return -ssto;
    }

    private Map<Integer,Set<Integer>> getPieces(List<Integer> partition) {
        // Create a partition from the input list
        final Partition partitionInstance = new Partition(partition);   
        // Get the partition pieces
        return partitionInstance.asSets();
    }

    }
    
