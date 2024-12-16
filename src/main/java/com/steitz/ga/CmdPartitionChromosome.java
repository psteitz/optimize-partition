package com.steitz.ga;

import java.util.List;

/**
 * A PartitionChromosome that computes fitness by executing a command in a bash
 * shell.
 */
public class CmdPartitionChromosome extends PartitionChromosome {

    // Command to execute
    private final String command;

    /**
     * Create a new CmdPartitionChromosome with the given partition representation.
     * and command.
     * 
     * @param representation partition represented as a List of integers
     * @param command        shell command to execute with representation as
     *                       argument
     */
    public CmdPartitionChromosome(List<Integer> representation, String command) {
        super(representation, new CmdPartitionFitness(command));
        this.command = command;
    }

    /***
     * Create a new CmdPartitionChromosome with the given partition representation.
     * 
     * @param representation partition represented as an array of Integers
     * @param command
     */
    public CmdPartitionChromosome(Integer[] representation, String command) {
        super(representation, new CmdPartitionFitness(command));
        this.command = command;
    }

    /**
     * Get the shell command to execute.
     * 
     * @return the shell command to execute
     */
    public String getCommand() {
        return command;
    }

    public PartitionFitness getPartitionFitness() {
        return new CmdPartitionFitness(command);
    }
}
