package com.steitz.ga;

import java.util.List;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;

public class ClusterPartitionOptimizer extends PartitionOptimizer {

    /**
     * PartitionOptimizer config
     * 
     * populationSize
     * numGenerations
     * tournamentArity
     * mutationRate
     * crossoverRate
     * elitismRate
     * 
     */
    private final PartitionOptimizerConfig partionOptimizerConfig;

    /**
     * Cluster problem
     * 
     * int numClusters
     * double[][] points;
     * 
     * 
     * 
     * 
     */
    private final ClusterProblem clusterProblem;

    public ClusterPartitionOptimizer(PartitionOptimizerConfig partitionOptimizerConfig, ClusterProblem clusterProblem) {
        this.partionOptimizerConfig = partitionOptimizerConfig;
        this.clusterProblem = clusterProblem;
    }

    @Override
    public Population getInitialPopulation(PartitionOptimizerConfig partitionOptimizerConfig, double[][] universe) {
        final int universeSize = universe.length;
        final int numClusters = clusterProblem.getNumClusters();
        final int populationSize = partitionOptimizerConfig.getPopulationSize();

        double x = partitionOptimizerConfig.getElitismRate();

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
    public StoppingCondition getStoppingCondition(PartitionOptimizerConfig config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStoppingCondition'");
    }

    @Override
    public GeneticAlgorithm createGeneticAlgorithm(PartitionOptimizerConfig config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGeneticAlgorithm'");
    }

    @Override
    public PartitionOptimizerConfig getPartionOptimizerConfig() {
        return partionOptimizerConfig;
    }

    @Override
    protected double[][] getUniverse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUniverse'");
    }

}
