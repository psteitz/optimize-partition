# optimize-partition
[![Package Status](https://img.shields.io/badge/status-experimental-yellow)](https://github.com/psteitz/optimize-partition)
[![License](https://img.shields.io/badge/license-apache2-green)](https://github.com/psteitz/greppy/blob/main/LICENSE)

## What is this?
Optimize-partition is a micro-framework for optimizing objective functions defined over partitions using the Genetic Algorithm.
The problem solved here is finding good partitions of a set given an objective function defined over partitions of the set.
The Genetic Algorithm is used to direct the search for partitions with high values of the objective function.

Suppose that you have a universal set $U$ and you are trying to find the best partition of $U$ under the partition fitness function $f$. Write $U = \\{u_0, ,,, u_{n-1}\\}$ where $n$ is the size of the universe.  Then a *partition* $p$ of $U$ is a collection of subsets of $U$ that are non-empty, collectively exhaustive of $U$, and mutually exclusive. We call the subsets in $p$ the *pieces* in the partition.  Partitions can be represented using integer arrays of length $n$, where for each $i$, $p[i]$ is the partition piece that $u_i$ belongs to.  

For example, suppose that $U = \\{0,1,2,3,4,5\\}$.  Consider the partition $p = \\{\\{0,1\\}, \\{2,3\\}, \\{4,5\\}\\}$ of $U$.
This partition is represented by the array $[0,0,1,1,2,2]$.

To find good partitions under $f$, we start by generating a random initial population of partitions and consider their array representations as chromosomes.
Then we apply the Genetic Algorithm with
 * Chromosome fitness defined by the fitness function applied to the represented partition
 * Crossover is defined using interleaving.  More precisely, if $[c_0, ..., c_n]$ and $[d_0, ..., d_n]$ are chromosomes,
   then their cross is $[e_0, ..., e_n]$ where for $0 \le i < n$, $e_i = c_i$ if $i$ is even and $d_i$ if $i$ is odd.
 * Mutation makes a random partition piece assignment change for a randomly selected element.

### Notes
* Both crossover and mutation can change the number of pieces in the partition.  When a piece is eliminated, the chromosome is recoded to 
  make sure that there are no "holes" in the representation.  For example, crossing $[0,1,2]$ with $[0,0,0]$ results in $[0,0,2]$ which gets
  recoded to $[0,0,1]$.
* The order of the pieces is significant in our representation, but set-theortically it is not.  For 
  example,  $[0,0,1,1,2,2]$,  $[1,1,0,0,2,2]$ and $[2,2,1,1,0,0]$ all represent the same partition set-theoretically, but the three
  chromosomes are different.  All have the same fitness. Each partion $p$ has $\left|{p}\right|{!}$ chromosome representations,
  where $\left|{p}\right|$ is the number of pieces in $p$.

## Usage

To use the framework you need to provide an objective function that takes a representation like above for a partition and returns the fitness of the partition represented by the input array. You can get your fitness function activated in one of three ways:
 1. **Implement PartitionFitness** in a Java class extending ```PartitionChromosome```
 1. Shell out to **command line** with configured command expecting space-delimited integer arguments representing a partition
 2. **http GET** to configured URL with querystring "?partition=" followed by partition represented by bracketed, comma-separated list of integers

Test classes provide examples for how to do each of these

| Activation | Test Class |
| -------- | ------- |
| Implement PartitionFitness | https://github.com/psteitz/optimize-partition/blob/main/src/test/java/com/steitz/ga/MaxValuePartitionChromosome.java |
| Command line | https://github.com/psteitz/optimize-partition/blob/main/src/test/java/com/steitz/ga/TestCmdPartitionChromosome.java |
| HTTP GET  | https://github.com/psteitz/optimize-partition/blob/main/src/test/java/com/steitz/ga/TestHttpPartitionChromosome.java |


## Dependencies
optimize-partition depends on Apache Commons Math, version 3.6.1

## Experiments
### Clustering
 1. Start with a universe of length 3 real vectors generated as follows.
    * Generate 5 random vectors making sure that no two of them are closer than 10 units apart.
      These will be the centroids of the clusters in the optimal partition.
    * For each centroid, generate 9 nearby points with gaussian component differences from the centroid.
      Make the standard deviation of the gaussian noise 0.1, so the deviates genearated around a centroid remain
      comparatively much closer to the centroid than to any neighboring centroid or any of its deviates.
      
2. Set the universe to the centroids, followed by the deviate blocks in order by centroid.

3. Set the objective function to be the sum of the squared pairwise euclidean distances between universe elements in the same partition piece.

4. Run the Genetic Algorithm starting with a random population of partitions with fitness defined by the objective function in 3. and verify that after 100 generations the fittest partition is the correct one (centroids and their deviates in each of 5 pieces).

Optimizing this objective function is the same as performing k-means clustering over the universe with $k=5$

#### Implementation
The ```testClusterPartitionChromosomeClusteredUniverse``` case in
https://github.com/psteitz/optimize-partition/blob/main/src/test/java/com/steitz/ga/TestClusterPartitionChromosome.java 
does 1-3 above and verifies that after 100 generations the best partitio is the k-means optimal one (each of 5 centroids and
their deviates make 5 partition pieces).

 
