package com.steitz.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a partition of a set.
 * <p>
 * If universe is a set of elements, a partition of the universe is an indexed
 * list of subsets of the universe that do not overlap and whose union is the
 * entire universe. So for example, if the universe is the set of integers from
 * 0 to 9, a partition of the universe might be the list of subsets
 * {0, 1, 2}, {3, 4, 5}, {6, 7, 8, 9}.
 * <p>
 * We call the subsets *pieces* and the number of pieces is the number of sets
 * in the partition.
 * <p>
 * Partitions are represented as arrays of integers of length equal to the size
 * of the universe and values equal to the index of the piece that the
 * corresponding element of the universe is in. For example, the above partition
 * is represented by the following array: [0, 0, 0, 1, 1, 1, 2, 2, 2, 2].
 */
public class Partition {
    /**
     * Domain of the partition - the number of elements
     * in the universe set that the partition divides up
     */
    final int n;

    /**
     * number of elements in the partition.
     * This isthe number of pieces that the universe
     * is divided into
     */
    int m;

    /**
     * the partition itself.
     * <p>
     * The partition is represented as an array of integers
     * where each integer is the index of the piece that the
     * corresponding element of the universe is in.
     * <p>
     * So if universe = {a, b, c, d, e} and partition is [0, 1, 0, 1, 1]
     * then a and c are in piece 0 and b, d, and e are in piece 1 so the partion is
     * {a, c}, {b, d, e}.
     */
    int[] partition;

    /**
     * Create a partition of the universe of size n into m pieces
     * from the given partition array.
     * 
     * @param partition the partition represented as an array of integers
     */
    public Partition(int[] partition) {

        this.n = partition.length;

        // Find the maximum value in the partition array
        int max = 0;
        for (int i = 0; i < n; i++) {
            if (partition[i] > max) {
                max = partition[i];
            }
        }

        // The number of pieces is one more than the maximum value in the partition
        // array. Values are 0, ..., m - 1.
        this.m = max + 1;

        // copy the input partition array
        this.partition = new int[n];
        System.arraycopy(partition, 0, this.partition, 0, n);

        // remove any empty pieces
        removeEmptyPieces();
    }

    /**
     * Create a Partition from a List of Integers
     * 
     * @param representation the partition represented as a list of integers
     */
    public Partition(List<Integer> representation) {
        this(representation.stream().mapToInt(i -> i).toArray());
    }

    /**
     * Create a random partition of the universe of size n into at most m pieces.
     * 
     * @param n the size of the universe
     * @param m the number of pieces in the partition
     * @return a random partition of the universe
     */
    public static Partition randomPartition(int n, int m) {
        final int[] partition = new int[n];
        for (int i = 0; i < n; i++) {
            partition[i] = (int) (Math.random() * m);
        }
        return new Partition(partition);
    }

    /**
     * Create a binary string representation of the partion.
     * <p>
     * The partition is represented by a sequence of fixed-size blocks of binary
     * digits. There are as many blocks as there are elements of the universe and
     * the number represented by the block is the index of the partition piece that
     * the corresponding element of the universe is in.
     * 
     * @return a binary string representation of the partition
     */

    public String toBinaryString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            final String binary = Integer.toBinaryString(partition[i]);
            sb.append("0".repeat(16 - binary.length())).append(binary);
        }
        return sb.toString();
    }

    public ArrayList<Integer> getRepresentation() {
        // Create a list of integers from the partition array
        return new ArrayList<>(Arrays.asList(Arrays.stream(partition).boxed().toArray(Integer[]::new)));
    }

    /**
     * Create a partition from a binary string representation.
     */
    public Partition fromBinaryString(String binaryString) {
        final int n = binaryString.length() / 16;
        final int[] partition = new int[n];
        for (int i = 0; i < n; i++) {
            partition[i] = Integer.parseInt(binaryString.substring(i * 16, (i + 1) * 16), 2);
        }
        return new Partition(partition);
    }

    /**
     * Given an element of the range of the partion, return the number of elements
     * in the universe that the partition maps to that element.
     */
    public int count(int piece) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (partition[i] == piece) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get piece counts as an array
     */
    public int[] pieceCounts() {
        final int[] counts = new int[m];
        for (int i = 0; i < n; i++) {
            counts[partition[i]]++;
        }
        return counts;
    }

    /**
     * When creating partitions by mutation, we may can end up with empty pieces.
     * Empty pieces make no sense, so we need to remove them. We remove them by
     * creating a new partition that is the same as the old partition but with
     * the empty pieces removed so m is the number of non-empty pieces.
     * 
     * If this partition has no empty pieces, this method returns a reference to
     * this; otherwise a new partion is created and returned.
     */
    protected void removeEmptyPieces() {
        final int[] counts = pieceCounts();
        // Count the number of non-empty pieces
        int numNonEmptyPieces = 0;
        for (int i = 0; i < m; i++) {
            if (counts[i] > 0) {
                numNonEmptyPieces++;
            }
        }
        if (numNonEmptyPieces == m) {
            // No empty pieces to remove
            return;
        }
        final int[] newPartition = new int[n];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (counts[partition[i]] > 0) {
                newPartition[j++] = partition[i];
            }
        }

        // Now collapse partition values so that they are contiguous

        // Fill sorted hashmap with universe id, label pairs.
        final TreeMap<Integer, Set<Integer>> values = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            if (!values.containsKey(partition[i])) {
                values.put(partition[i], new HashSet<>());
            }
            values.get(partition[i]).add(i);
        }

        // Create a map of old partition values to new partition values
        // Keys are old partition values and values are new partition values
        final HashMap<Integer, Integer> collapsingMap = new HashMap<>();
        final AtomicInteger nDistinct = new AtomicInteger(0);
        values.descendingKeySet().forEach((Integer key) -> {
            collapsingMap.put(key, nDistinct.getAndIncrement());
        });

        // update m - some pieces may have been removed
        m = nDistinct.get();

        // Now go through the partition and replace old values with new values
        for (int i = 0; i < n; i++) {
            newPartition[i] = collapsingMap.get(partition[i]);
        }

        this.partition = newPartition;
    }

    /**
     * @return the size of the universe
     */
    public int getN() {
        return n;
    }

    /**
     * @return the number of pieces in the partition
     */
    public int getM() {
        return m;
    }

    /**
     * Get the partition represented as an array of integers.
     * 
     * If a is the output array, then a[i] = j means that the jth piece
     * containscom.steitz.ga.Partition.pieceCounts(Partition.java:163)
     * a_i.
     * 
     * @return the partition represented as an array of integers.
     */
    public int[] getPartition() {
        return partition;
    }

    /**
     * Get the partition as a hashmap of sets of elements of the universe. Keys are
     * partition indexes and values are sets of elements in that partition piece.
     * 
     * @return Map with keys partition piece numbers and values sets of elements in
     *         that piece.
     */
    public HashMap<Integer, Set<Integer>> asSets() {
        final HashMap<Integer, Set<Integer>> sets = new HashMap<>();
        // For each piece, create a set of the elements in that piece
        for (int i = 0; i < n; i++) {
            final int piece = partition[i];
            if (!sets.containsKey(piece)) {
                sets.put(piece, new HashSet<>());
            }
            sets.get(piece).add(i);
        }
        return sets;
    }
}
