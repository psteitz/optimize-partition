package com.steitz.ga;

public class ClusterPartitionOptimizerConfig extends PartitionOptimizerConfig {
    private final int numClusters;

    public ClusterPartitionOptimizerConfig(ClusterPartitionOptimizerConfig.Builder builder) {
        super(builder);
        this.numClusters = builder.getNumClusters();
    }

    public ClusterPartitionOptimizerConfig(Builder builder, int numClusters) {
        super(builder);
        this.numClusters = numClusters;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public static class Builder extends PartitionOptimizerConfig.Builder {

        private int numClusters;

        public Builder() {
        }

        public Builder numClusters(int numClusters) {
            this.numClusters = numClusters;
            return this;
        }

        public int getNumClusters() {
            return numClusters;
        }

        public ClusterPartitionOptimizerConfig build(int numClusters) {
            return new ClusterPartitionOptimizerConfig(this);
        }
    }
}