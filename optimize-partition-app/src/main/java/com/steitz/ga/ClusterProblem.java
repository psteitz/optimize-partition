package com.steitz.ga;

/**
 * Clustering problem specification.
 */

public class ClusterProblem {
    /** Number of clusters */
    private final int numClusters;
    /** Number of points */
    private final int numPoints;
    /** Points to cluster */
    private final double[][] points;

    /**
     * Create a new clustering problem.
     * 
     * @param numClusters number of clusters
     * @param points      points to cluster
     */
    public ClusterProblem(int numClusters, double[][] points) {
        this.numClusters = numClusters;
        this.numPoints = points.length;
        this.points = points;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public int getNumPoints() {
        return numPoints;
    }

    public double[] getPoint(int i) {
        return points[i];
    }

    /**
     * Get a copy of the points array.
     * 
     * @return a copy of the points array
     */
    protected double[][] getPoints() {
        double[][] copy = new double[points.length][];
        for (int i = 0; i < points.length; i++) {
            copy[i] = new double[points[i].length];
            System.arraycopy(points[i], 0, copy[i], 0, points[i].length);
        }
        return copy;
    }

}
