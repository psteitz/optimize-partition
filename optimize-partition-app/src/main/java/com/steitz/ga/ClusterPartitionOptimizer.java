package com.steitz.ga;

import java.util.List;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClusterPartitionOptimizer extends PartitionOptimizer {

    /**
     * PartitionOptimizer config
     */
    private final PartitionOptimizerConfig partionOptimizerConfig;

    /**
     * Cluster problem
     */
    private final ClusterProblem clusterProblem;

    private final double[][] universe;

    public ClusterPartitionOptimizer(PartitionOptimizerConfig partitionOptimizerConfig, ClusterProblem clusterProblem,
            double[][] universe) {
        this.partionOptimizerConfig = partitionOptimizerConfig;
        this.clusterProblem = clusterProblem;
        this.universe = universe;
    }

    @Override
    public Population getInitialPopulation(PartitionOptimizerConfig partitionOptimizerConfig) {
        final int universeSize = universe.length;
        final int numClusters = clusterProblem.getNumClusters();
        final int populationSize = partitionOptimizerConfig.getPopulationSize();

        final ClusterPartitionChromosome[] chromosomes = new ClusterPartitionChromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            final Partition randomPartition = Partition.randomPartition(universeSize, numClusters);
            final List<Integer> representation = randomPartition.getRepresentation();
            chromosomes[i] = new ClusterPartitionChromosome(representation, universe[0].length, universe);
        }
        final Population out = new ElitisticListPopulation(populationSize, partitionOptimizerConfig.getElitismRate());
        for (int i = 0; i < populationSize; i++) {
            out.addChromosome(chromosomes[i]);
        }
        return out;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return new FixedGenerationCount(partionOptimizerConfig.getNumGenerations());
    }

    @Override
    public GeneticAlgorithm createGeneticAlgorithm(PartitionOptimizerConfig config) {
        // initialize a new genetic algorithm
        final ClusterPartitionFitness fitness = new ClusterPartitionFitness(universe[0].length, universe);
        return new GeneticAlgorithm(
                new PartitionChromosomeCrossover(fitness),
                partionOptimizerConfig.getCrossoverRate(),
                new PartitionChromosomeMutation(fitness),
                partionOptimizerConfig.getMutationRate(),
                new TournamentSelection(partionOptimizerConfig.getTournamentArity()));
    }

    @Override
    public PartitionOptimizerConfig getPartionOptimizerConfig() {
        return partionOptimizerConfig;
    }

    protected double[][] getUniverse() {
        // Return a copy of the universe
        final double[][] out = new double[universe.length][];
        for (int i = 0; i < universe.length; i++) {
            out[i] = universe[i].clone();
        }
        return out;
    }

    /**
     * Main method for the ClusterPartitionOptimizer.
     * 
     * Takes two command-line arguments: universeFilePath and configFilePath
     * 
     * universefilePath is file path to a CSV file
     * containing the universe.
     * 
     * configFilePath is file path to a json file with two sections
     * one for the ClusterProblem and one for the
     * PartitionOptimizerConfig.
     * 
     * ClusterProblem hasone field: numClusters
     * 
     * PartitionOptimizerConfig section has six fields:
     * populationSize, numGenerations, tournamentArity,
     * mutationRate, crossoverRate, elitismRate
     * 
     * Here is an example of a JSON config file:
     * {
     * "ClusterProblem": {
     * "numClusters": 3
     * },
     * "PartitionOptimizerConfig": {
     * "populationSize": 100,
     * "numGenerations": 100,
     * "tournamentArity": 2,
     * "mutationRate": 0.01,
     * "crossoverRate": 0.8,
     * "elitismRate": 0.1
     * }
     * }
     * 
     * ```java ClusterPartitionOptimizer universe.csv config.json``` reads
     * the universe from universe.csv and the config from config.json and runs the
     * optimization.
     * 
     * @param args command line arguments
     * 
     */
    public static void main(String[] args) {

        // Parse the command line arguments
        final String universeFilePath = args[0];
        final String configFilePath = args[1];

        // universe is a 2D array of doubles
        // Each row is a point in the universe
        // the dimensionality of the universe is the number of columns

        // To allocate universe, we need to know the number of rows and columns.
        // We read the universe file twice
        // First to count the number of rows and columns
        // Then to read the data into the universe array

        // Count the number of rows and columns in the universe file
        int numPoints = 0;
        int dimension = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(universeFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (dimension == 0) {
                    dimension = values.length;
                }
                numPoints++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading universe file", e);
        }

        // Allocate the universe array. Rows correspond to points, columns to
        // dimensions.
        final double[][] universe = new double[numPoints][dimension];

        // Open universe file and read it into the universe array
        try (BufferedReader br = new BufferedReader(new FileReader(universeFilePath))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                final String[] values = line.split(",");
                for (int j = 0; j < values.length; j++) {
                    universe[i][j] = Double.parseDouble(values[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading universe file", e);
        }

        // Load the ClusterProblem and PartitionOptimizerConfig from the JSON file
        ClusterProblem clusterProblem = null;
        PartitionOptimizerConfig partitionOptimizerConfig = null;

        // Open config file and read it into the clusterProblem and
        // partitionOptimizerConfig objects
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            JSONParser jsonParser = new JSONParser();
            try {
                final JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(configFilePath));
                final JSONObject clusterProblemJson = (JSONObject) jsonObject.get("ClusterProblem");
                final JSONObject partitionOptimizerConfigJson = (JSONObject) jsonObject.get("PartitionOptimizerConfig");
                ClusterProblem.ClusterProblemConfig clusterProblemConfig = new ClusterProblem.ClusterProblemConfig(
                        Integer.parseInt(clusterProblemJson.get("numClusters").toString()));
                clusterProblem = new ClusterProblem(clusterProblemConfig, universe);

                // Extract the partition optimizer config
                final int populationSize = Integer
                        .parseInt(partitionOptimizerConfigJson.get("populationSize").toString());
                final int numGenerations = Integer
                        .parseInt(partitionOptimizerConfigJson.get("numGenerations").toString());
                final int tournamentArity = Integer
                        .parseInt(partitionOptimizerConfigJson.get("tournamentArity").toString());
                final double mutationRate = Double
                        .parseDouble(partitionOptimizerConfigJson.get("mutationRate").toString());
                final double crossoverRate = Double
                        .parseDouble(partitionOptimizerConfigJson.get("crossoverRate").toString());
                final double elitismRate = Double
                        .parseDouble(partitionOptimizerConfigJson.get("elitismRate").toString());
                // Create the partition optimizer config
                partitionOptimizerConfig = new PartitionOptimizerConfig.Builder()
                        .populationSize(populationSize)
                        .numGenerations(numGenerations)
                        .tournamentArity(tournamentArity)
                        .mutationRate(mutationRate)
                        .crossoverRate(crossoverRate)
                        .elitismRate(elitismRate)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create a ClusterPartitionOptimizer
        final ClusterPartitionOptimizer clusterPartitionOptimizer = new ClusterPartitionOptimizer(
                partitionOptimizerConfig, clusterProblem, universe);

        // Execute the optimization
        clusterPartitionOptimizer.execute();
    }

}
