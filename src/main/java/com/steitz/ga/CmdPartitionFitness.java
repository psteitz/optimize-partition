package com.steitz.ga;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculate the fitness of a partition by executing a command in a bash shell
 * with the partition as csv list as argument.
 */
public class CmdPartitionFitness implements PartitionFitness {
    /**
     * Maximum number of history entries.
     */
    protected static final int HISTORY_SIZE = 10;

    /**
     * History of fitness <args, fitness> pairs where fitness is what is rturned by
     * command "args"
     */
    protected final Map<String, Double> history = new HashMap<String, Double>();

    // Command to execute
    private final String command;

    /**
     * Create a new CmdPartitionFitness with the given command.
     */
    public CmdPartitionFitness(String command) {
        this.command = command;
    }

    /**
     * Compute fitness by executing command in a bash shell with the partition as
     * csv list as argument.
     */
    @Override
    public double fitness(List<Integer> partition) {
        // Execute command in a bash shell with space delimited list of values in
        // partiton as command line arguments.
        // Parse the shell output as a double and return it.

        // Strip [ and ] from toString output and replace commas with spaces
        final String args = partition.toString().replaceAll("[\\[\\]]", "").replaceAll(",", " ");

        final Process process;
        final StringBuilder output = new StringBuilder();

        // Execute command in a bash shell with space delimited list of partition values
        // as command-line arguments
        // Capture the output of the command and parse it as a double.
        try {
            process = Runtime.getRuntime().exec(command + " " + args);
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
            ret = Double.parseDouble(output.toString());
        } catch (NumberFormatException e) {
            System.out.println(dumpHistory());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse output as double: " + output.toString());
        }
        if (history.size() == HISTORY_SIZE) {
            // Remove the oldest entry
            history.remove(history.keySet().iterator().next());
        }
        // Update history with new activation reccord
        history.put(command + " " + '"' + args + '"', ret);
        return ret;
    }

    public String dumpHistory() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : history.entrySet()) {
            sb.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
