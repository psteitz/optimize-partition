public abstract class PartitionOptimizer {
    /**
     * Generate an initial population of chromosomes from a universe of vectors
     */
    public abstract Population getInitialPopulation();

    /**
     * Create GA instance
     */
    public abstract GeneticAlgorithm createGeneticAlgorithm();

    public void execute() {
        // Generate initial population
        final Population initialPopulation = getInitialPopulation();

        // Set stopping condition
        final StoppintCondition stoppingCondition = getStoppingCondition();
        
        // run the algorithm
        Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome bestFinal = finalPopulation.getFittestChromosome();

        // Display results
        System.out.println("Best fitness: " + bestFinal.fitness());
        System.out.println("Best Partition:");
        System.out.println(bestFinal);
    }
}
