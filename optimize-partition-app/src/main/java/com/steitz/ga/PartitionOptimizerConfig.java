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

    public PartitionOptimizerConfig(int populationSize, int numGenerations, double mutationRate, double crossoverRate,
            double elitismRate, int tournamentArity) {
        this.populationSize = populationSize;
        this.numGenerations = numGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismRate = elitismRate;
        this.tournamentArity = tournamentArity;
    }

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
            return new PartitionOptimizerConfig(populationSize, numGenerations, mutationRate, crossoverRate,
                    elitismRate,
                    tournamentArity);
        }
    }
}