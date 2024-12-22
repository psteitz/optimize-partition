package com.steitz.ga;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.junit.jupiter.api.Test;

public class TestCmdPartitionChromosome {

    /**
     * SUM_COMMAND executes bash script that sums command line arguments and returns
     * the sum.
     */
    private final static String SUM_COMMAND = Paths.get("src/test/resources/sum.sh").toAbsolutePath().toString();
    /**
     * NEG_SUM_COMMAND returns negative of SUM_COMMAND. Highest possible value is 0.
     */
    private final static String NEG_SUM_COMMAND = Paths.get("src/test/resources/negSum.sh").toAbsolutePath().toString();

    private Population getInitialPopulation(String command) {

        // Set the size of the universe
        final int N = 100;

        // Set the number of pieces to divide the universe into
        final int M = 10;

        // Set the size of the initial population
        final int POPULATION_SIZE = 1000;

        // Create the initial population
        CmdPartitionChromosome[] chromosomes = new CmdPartitionChromosome[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            final Partition randomPartition = Partition.randomPartition(N, M);
            final List<Integer> representation = randomPartition.getRepresentation();
            chromosomes[i] = new CmdPartitionChromosome(representation, command);
        }

        final Population out = new ElitisticListPopulation(chromosomes.length, 0.1);

        for (PartitionChromosome chromosome : chromosomes) {
            out.addChromosome(chromosome);
        }

        return out;
    }

    @Test
    public void testOptimizeCmdPartitionHighestSum() {
        System.out.println("Starting..");

        // Set the tournament arity - the number of chromosomes to include in each
        // population
        final int TOURNAMENT_ARITY = 1000;

        // Set the number of generations to run the genetic algorithm
        final int NUM_GENERATIONS = 100;

        final CmdPartitionFitness fitness = new CmdPartitionFitness(SUM_COMMAND);

        // initialize a new genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new PartitionChromosomeCrossover(fitness),
                1,
                new PartitionChromosomeMutation(fitness),
                0.10,
                new TournamentSelection(TOURNAMENT_ARITY));

        // initial population
        System.out.println("Generating initial population");
        Population initial = getInitialPopulation(SUM_COMMAND);
        System.out.println("Generated initial population");

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        System.out.println("Running genetic algorithm");
        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();
        // The highest possible fitness comes from [0,1,2,3,4,5,6,7,8,9,,,,,,9]
        // Length 100, sums to 36 + 9 * 91 = 855
        System.out.println(fitness.dumpHistory());
        System.out.println("Best fitness: " + bestFinal.fitness());
        assertTrue(bestFinal.fitness() > 850.0);
    }

    @Test
    public void testOptimizeCmdPartitionNegSum() {
        // Reset command to min.sh - shell script that returns -1 * sum of its arguments
        System.out.println("Starting");

        // Set the tournament arity - the number of chromosomes to include in each
        // population
        final int TOURNAMENT_ARITY = 1000;

        // Set the number of generations to run the genetic algorithm
        final int NUM_GENERATIONS = 100;

        final CmdPartitionFitness fitness = new CmdPartitionFitness(NEG_SUM_COMMAND);

        // initialize a new genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new PartitionChromosomeCrossover(fitness),
                1,
                new PartitionChromosomeMutation(fitness),
                0.10,
                new TournamentSelection(TOURNAMENT_ARITY));

        // initial population
        System.out.println("Generating initial population");
        Population initial = getInitialPopulation(NEG_SUM_COMMAND);
        System.out.println("Initial population generated");

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();
        // The highest possible fitness comes from [0,...,0]
        // Length 100, sums to 0
        System.out.println(fitness.dumpHistory());
        System.out.println("Best fitness: " + bestFinal.fitness());
        assertTrue(bestFinal.fitness() > -10);
    }
}
