package com.steitz.ga;

import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;

import org.junit.Before;
import org.junit.Test;

public class TestClusterPartitionOptimizer {

    private static String TEST_FILE_NAME = "universe.csv";

    private static ReentrantLock fileLock = new ReentrantLock();

    @GuardedBy("fileLock")
    private static AtomicBoolean setupDone = new AtomicBoolean(false);

    /**
     * Setup
     * Create or overwrite TEST_FILE_NAME with random universe. Do this just once.
     */
    @Before
    public void setUp() {
        final double[][] universe = ClusterPartitionUtils.randomClusteredUniverse(100, 5, 10.0, 0.1, 3);
        // Get $HOME
        final String home = System.getProperty("user.home");
        try {
            fileLock.lock();
            System.out.println("Creating test universe file: " + home
                    + "/optimize-partition/optimize-partition-app/src/test/resources/" + TEST_FILE_NAME);
            ClusterPartitionUtils.universe2csv(universe,
                    home + "/optimize-partition/optimize-partition-app/src/test/resources/" + TEST_FILE_NAME);
            setupDone.set(true);
            System.out.println("Test universe file created.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            fileLock.unlock();
        }
    }

    @Test
    public void testClusterPartitionOptimizer() {
        // Load the test universe
        final String universeFilePath = System.getProperty("user.home")
                + "/optimize-partition/optimize-partition-app/src/test/resources/" + TEST_FILE_NAME;
        final String configFilePath = System.getProperty("user.home")
                + "/optimize-partition/optimize-partition-app/src/test/resources/cluster-optimizer.json";
    }
}