package com.steitz.ga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.junit.jupiter.api.Test;

/**
 * Optimize an objective function defined over partitons of a set.
 * <p>
 * Goal is to find the partition that maximizes the objective function.
 * Uses Genetic Algorithm to search for the optimal partition.
 */
public class TestOptimizePartition {

    private Population getInitialPopulation() {

        // Set the size of the universe
        final int N = 100;

        // Set the number of pieces to divide the universe into
        final int M = 10;

        // Set the size of the initial population
        final int POPULATION_SIZE = 1000;

        // Create the initial population
        MaxValuePartitionChromosome[] chromosomes = new MaxValuePartitionChromosome[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            chromosomes[i] = new MaxValuePartitionChromosome(Partition.randomPartition(N, M).getRepresentation());
        }

        final Population out = new ElitisticListPopulation(chromosomes.length, 0.1);

        for (PartitionChromosome chromosome : chromosomes) {
            out.addChromosome(chromosome);
        }

        return out;
    }

    @Test
    public void testOptimizeMaxValuePartition() {
        System.out.println("Generating initial population");

        // Set the tournament arity - the number of chromosomes to include in each
        // population
        final int TOURNAMENT_ARITY = 1000;

        // Set the number of generations to run the genetic algorithm
        final int NUM_GENERATIONS = 100;

        // initialize a new genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new PartitionChromosomeCrossover(new MaxValuePartitionChromosome.MaxValuePartitionFitness()),
                1,
                new PartitionChromosomeMutation(new MaxValuePartitionChromosome.MaxValuePartitionFitness()),
                0.10,
                new TournamentSelection(TOURNAMENT_ARITY));

        // initial population
        Population initial = getInitialPopulation();

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();
        // System.out.println("Best fitness: " + bestFinal.fitness());
        // System.out.println(bestFinal.toString());
        assertEquals(bestFinal.fitness(), 50.0, 0.0);
    }
}
