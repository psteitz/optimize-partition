package com.steitz.ga;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

/**
 * Abstract class for partition optimization using the genetic algorithm.
 * <p>
 * Implementations provide initial population, stopping condition, and GA
 * instance.
 */
public abstract class PartitionOptimizer {

    /**
     * Get the configuration for the partition optimization algorithm.
     */
    public abstract PartitionOptimizerConfig getPartionOptimizerConfig();

    /**
     * Generate an initial population of chromosomes representing partitions of the
     * universe.
     * <p>
     * Rows are double arrays of length <dimension>. Each row is an element of the
     * universe.
     * <p>
     * Partitions are sets of dimension-length double arrays from the universe.
     */
    public abstract Population getInitialPopulation(PartitionOptimizerConfig config);

    /**
     * Get the stopping condition for the genetic algorithm
     */
    public abstract StoppingCondition getStoppingCondition();

    /**
     * Create GA instance
     */
    public abstract GeneticAlgorithm createGeneticAlgorithm(PartitionOptimizerConfig config);

    /**
     * Execute the partition optimization algorithm.
     * <p>
     * Default implementation writes the best fitness and partition to the console.
     */
    public void execute() {
        // Generate initial population
        final Population initialPopulation = getInitialPopulation(getPartionOptimizerConfig());

        // Set stopping condition
        final StoppingCondition stoppingCondition = getStoppingCondition();

        // run the algorithm
        final Population finalPopulation = createGeneticAlgorithm(
                getPartionOptimizerConfig()).evolve(initialPopulation, stoppingCondition);

        // best chromosome from the final population
        final Chromosome bestFinal = finalPopulation.getFittestChromosome();

        // Display results
        System.out.println("Best fitness: " + bestFinal.fitness());
        System.out.println("Best Partition:");
        System.out.println(bestFinal);
    }
}
