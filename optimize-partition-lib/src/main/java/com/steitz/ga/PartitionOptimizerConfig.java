package com.steitz.ga;

/**
 * Configuration class for PartitionOptimizer
 */

public class PartitionOptimizerConfig {
    private final int populationSize;
    private final int numGenerations;
    private final int tournamentArity;
    private final double mutationRate;
    private final double crossoverRate;
    private final double elitismRate;

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public double getElitismRate() {
        return elitismRate;
    }

    public int getTournamentArity() {
        return tournamentArity;
    }

    public String toString() {
        return "Population size: " + populationSize + ", Number of generations: " + numGenerations
                + ", Tournament arity: "
                + tournamentArity + ", Mutation rate: " + mutationRate + ", Crossover rate: " + crossoverRate
                + ", Elitism rate: "
                + elitismRate;
    }

    /**
     * Construct a new builder for PartitionOptimizerConfig using a builder instance
     */
    public PartitionOptimizerConfig(Builder builder) {
        this.populationSize = builder.populationSize;
        this.numGenerations = builder.numGenerations;
        this.tournamentArity = builder.tournamentArity;
        this.mutationRate = builder.mutationRate;
        this.crossoverRate = builder.crossoverRate;
        this.elitismRate = builder.elitismRate;
    }

    /**
     * Builder class for PartitionOptimizerConfig
     */
    public static class Builder {
        private int populationSize;
        private int numGenerations;
        private int tournamentArity;
        private double mutationRate;
        private double crossoverRate;
        private double elitismRate;

        public Builder() {
        }

        public Builder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public Builder numGenerations(int numGenerations) {
            this.numGenerations = numGenerations;
            return this;
        }

        public Builder tournamentArity(int tournamentArity) {
            this.tournamentArity = tournamentArity;
            return this;
        }

        public Builder mutationRate(double mutationRate) {
            this.mutationRate = mutationRate;
            return this;
        }

        public Builder crossoverRate(double crossoverRate) {
            this.crossoverRate = crossoverRate;
            return this;
        }

        public Builder elitismRate(double elitismRate) {
            this.elitismRate = elitismRate;
            return this;
        }

        public PartitionOptimizerConfig build() {
            return new PartitionOptimizerConfig(this);
        }
    }
}