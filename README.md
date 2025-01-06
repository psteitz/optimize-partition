# optimize-partition
[![Package Status](https://img.shields.io/badge/status-experimental-yellow)](https://github.com/psteitz/optimize-partition)
[![License](https://img.shields.io/badge/license-apache2-green)](https://github.com/psteitz/greppy/blob/main/LICENSE)

## What is this?
Optimize-partition is a micro-framework for optimizing objective functions defined over partitions using the Genetic Algorithm.
The problem solved here is finding good partitions of a set given an objective function defined over partitions of the set.
The Genetic Algorithm is used to direct the search for partitions with high values of the objective function.

Suppose that you have a universal set $U$ and you are trying to find the best partition of $U$ under the partition fitness function $f$. Write $U = \\{u_0, ,,, u_n\\}$ where $n$ is the size of the universe.  Then a *partition* $p$ of $U$ is a collection of subsets of $U$ that are non-empty, collectively exhaustive of $u$, and mutually exclusive. We call the subsets in $p$ the *pieces* in the partition.  Partitions can be represented using integer arrays of length $n$, where for each $i$, $p[i]$ is the partition piece that $u_i$ belongs to.  

For example, suppose that $U = \\{0,1,2,3,4,5\\}$.  Consider the partition $p = \\{\\{0,1\\}, \\{2,3\\}, \\{4,5\\}\\}$ of $U$.
This partition is represented by the array $[0,0,1,1,2,2]$.

To find good partitions under $f$, we start by generating a random initial population of partitions and consider their array representations as chromosomes.
Then we apply the Genetic Algorithm with
 * Chromosome fitness defined by the fitness function applied to the represented partition
 * Crossover is defined using interleaving.  More precisely, if $[c_0, ..., c_n]$ and $[d_0, ..., d_n]$ are chromosomes,
   then their cross is $[e_0, ..., e_n]$ where for $0 \le i < n$, $e_i = c_i$ if $i$ is even and $d_i$ if $i$ is odd.
 * Mutation makes a random partition piece assignment change for a randomly selected element.

Both crossover and mutation can change the number of pieces in the partition.  When a piece is eliminated, the chromosome is recoded to make sure that there are no "holes" in the representation.  For example, crossing $[0,1,2]$ with $[0,0,0]$ results in $[0,0,2]$ which gets recoded to $[0,0,1]$.  Note also that while the order of the pieces is significant in our representation, set-theortically it is not.  For example,  $[0,0,1,1,2,2]$,  $[1,1,0,0,2,2]$ and $[2,2,1,1,0,0]$ all represent the same partition set-theoretically, but the three chromosomes are different.  All have the same fitness.

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



 
