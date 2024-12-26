package com.steitz.ga;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Calculate the fitness of a partition by executing a command in a bash shell
 * with the partition as csv list as argument.
 * 
 * The command should return string representation of double that is the fitness
 * of the partition.
 * 
 * Maintains a cache of computed fitness values
 */
public class CmdPartitionFitness implements PartitionFitness {
    /**
     * Cache size for fitness history
     */
    protected static final int FITNESS_CACHE_SIZE = 10000;

    /**
     * Cache of fitness <args, fitness> pairs where fitness is what is returned by
     * `command "args"`
     */
    protected final Map<String, Double> fitnessCache = new ConcurrentHashMap<>();

    // Command to execute
    private final String command;

    /**
     * History
     */
    public CmdPartitionFitness(String command) {
        this.command = command;
    }

    /**
     * Compute fitness by executing command in a bash shell with the partition as
     * csv list as argument.
     * 
     * Forks an OS process to execute command with partition as quoted command line
     * argument.
     * 
     * @param partition partition to calculate fitness of
     * @return fitness of partition
     */
    @Override
    public double fitness(List<Integer> partition) {
        // Execute command in a bash shell with space delimited list of values in
        // partiton as command line arguments.
        //
        // Parse the shell output as a double and return it.

        // Strip [ and ] from toString output and replace commas with spaces
        final String args = partition.toString().replaceAll("[\\[\\]]", "").replaceAll(",", " ");

        // See if we have the value in cache. If so, return it.
        final Double getCached = fitnessCache.get(command + " " + '"' + args + '"');
        if (getCached != null) {
            return getCached;
        }

        // Execute command in a bash shell with space delimited list of partition values
        // as command-line arguments.
        //
        // Capture the output of the command and parse it as a double.

        // OS process
        final Process process;
        // Output of the process
        final StringBuilder output = new StringBuilder();

        try {
            /// Get OS process for command with args
            process = Runtime.getRuntime().exec(command + " " + args);
            // Read command output into output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute command: " + command);
        }
        double ret;
        try {
            // output should be a double
            ret = Double.parseDouble(output.toString());
        } catch (NumberFormatException e) {
            System.out.println(dumpFitnessCache());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse output as double: " + output.toString());
        }
        if (fitnessCache.size() == FITNESS_CACHE_SIZE) {
            // Remove the oldest entry in the fitness cache
            fitnessCache.remove(fitnessCache.keySet().iterator().next());
        }
        // Update fitness cache with new activation reccord
        fitnessCache.put(command + " " + '"' + args + '"', ret);
        return ret;
    }

    /**
     * Dump the fitness cache to a string with one entry per line.
     * Entries are of the form "partition -> fitness"
     * where fitness is a double and partition is a comma separated list of integers
     * surrounded by square brackets.
     */
    public String dumpFitnessCache() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : fitnessCache.entrySet()) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
