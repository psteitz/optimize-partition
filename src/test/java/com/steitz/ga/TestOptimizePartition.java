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
 * Tests partition optimization with some example fitness functions.
 * 
 * First returns the sum of the partition.
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

    /**
     * Test optimizing a partition for the following fitness function:
     * 
     * Suppose that the universe is the set {u_0, u_1, ..., u_n} where each u_i is
     * a double and all are distict. So n is the size of the universe.
     * 
     * Define the fitness of a partition to be the sum of the maximum u_i value in
     * each partition piece.
     * 
     * Let p_0, ..., p_{m-1} be the pieces of the partition. So their union is the
     * universe and m is the number of pieces defined by the partition. For each
     * piece p_i, let m_i be the maximum value among universe
     * elements in p_i.
     * 
     * Return m_0 + m_1 + ... + m_{m-1}.
     * 
     * So for example, if the partition is
     * 
     * {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {9, 10, 11}, {12, 13, 14}
     * 
     * and the values are
     * 
     * {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14},
     * 
     * then the fitness of the partition is
     * 
     * 2 + 5 + 8 + 11 + 14 = 40.
     * 
     * @see MaxValuePartitionChromosome.MaxValuePartitionFitness
     */
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
        System.out.println("Generated initial population");

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        System.out.println("Running genetic algorithm");
        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();
        // System.out.println("Best fitness: " + bestFinal.fitness());
        // System.out.println(bestFinal.toString());
        assertEquals(bestFinal.fitness(), 50.0, 0.0);
    }
}
