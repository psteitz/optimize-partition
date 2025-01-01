# optimize-partition
[![Package Status](https://img.shields.io/badge/status-experimental-yellow)](https://github.com/psteitz/optimize-partition)
[![License](https://img.shields.io/badge/license-apache2-green)](https://github.com/psteitz/greppy/blob/main/LICENSE)

## What is this?
Optimize-partition is a micro-framework for optimizing objective functions defined over partitions using the genetic algorithm.
The problem solved here is finding the best partition of a set given an objective function defined over partitions of the set.
The number of partitions of a set grows very rapidly as the set grows, so it is in general intractable to examine all
partitions to find the best one.  The genetic algorithm is used to focus the search.

Suppose that you have a universal set $U$ and you are trying to find the best partition of $U$ under the partition fitness function $f$. Write $U = \\{u_0, ,,, u_n\\}$ where $n$ is the size of the universe.  Then a partition $p$ of $U$ is a collection of subsets of $U$ that are non-empty, collectively exhaustive of $u$, and mutually exclusive. We call the subsets in $p$ the $pieces$ in the partition.  Partitions can be represented using integer arrays of length $n$, where for each $i$, $p[i]$ is the partition piece that the partition assigns $u_i$ to.  

For example, suppose that $U = \\{0,1,2,3,4,5\\}$.  Consider the partition $p = \\{\\{0,1\\}, \\{2,3\\}, \\{4,5\\}\\}$ of $U$.
This partition is represented by the array $[0,0,1,1,2,2]$.

## Main Features

## Dependencies
optimize-partition depends on Apache Commons Math, version 3.6.1

## Documentation


 
