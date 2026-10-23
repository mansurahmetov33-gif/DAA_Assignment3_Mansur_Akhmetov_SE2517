# Assignment 3 — Graph Algorithms: Implementation and Performance Analysis

**Student:** Mansur Akhmetov  
**Group:** SE-2517  
**Course:** Design and Analysis of Algorithms  
**Project:** DAA Assignment 3 — DSU, MST, SCC, Topological Sorting and DAG Shortest Paths

---

## 1. Introduction

The purpose of this assignment is to implement, test, and experimentally evaluate fundamental graph algorithms. The project focuses on Disjoint Set Union (DSU), Minimum Spanning Tree (MST), Strongly Connected Components (SCC), topological sorting, and shortest paths in Directed Acyclic Graphs (DAGs).

All algorithms were implemented in Java using adjacency lists. JUnit 5 was used for correctness testing, while Python with Matplotlib and Pandas was used to visualize benchmark results.

The main objectives were to verify algorithm correctness, compare theoretical and practical complexity, and examine performance on graphs of different sizes and densities.

## 2. Algorithms and Implementation

### 2.1 Disjoint Set Union (DSU)

DSU maintains a collection of disjoint sets and supports two main operations: `find(x)`, which identifies the representative of a set, and `union(x, y)`, which merges two sets.

The implementation uses path compression and union by size. These optimizations provide nearly constant amortized time per operation, expressed as \(O(\alpha(V))\), where \(\alpha\) is the inverse Ackermann function.

DSU is used by Kruskal's algorithm to detect whether adding an edge would create a cycle.

### 2.2 Kruskal's Algorithm

Kruskal constructs a minimum spanning tree by sorting all edges in nondecreasing order of weight and selecting edges that connect different components.

For every edge, DSU determines whether its endpoints already belong to the same component. Edges creating cycles are rejected.

Its time complexity is \(O(E\log E)\), dominated by edge sorting. For disconnected graphs, the algorithm produces a minimum spanning forest.

### 2.3 Prim's Algorithm

Prim builds a minimum spanning tree by repeatedly selecting the minimum-weight edge connecting a visited vertex to an unvisited vertex.

The implementation uses a priority queue and an adjacency list. For disconnected graphs, it restarts from an unvisited vertex to produce a minimum spanning forest.

Its time complexity is \(O(E\log V)\) for the priority-queue-based implementation.

### 2.4 Kosaraju's Algorithm

Kosaraju identifies strongly connected components in a directed graph using two DFS passes.

The first pass records vertices in finishing order. The graph is then transposed, and the second pass processes vertices in reverse finishing order to identify SCCs.

Both passes use iterative DFS to avoid recursive stack overflow on large graphs.

The time complexity is \(O(V+E)\).

### 2.5 Kahn's Algorithm

Kahn performs topological sorting using the indegree of each vertex and a queue.

Initially, all vertices with indegree zero are added to the queue. Each processed vertex is removed conceptually from the graph, and its outgoing neighbors have their indegrees decreased.

If fewer than \(V\) vertices are processed, the graph contains a directed cycle.

The time complexity is \(O(V+E)\).

### 2.6 DAG Shortest Paths

Shortest paths in a DAG are computed by processing vertices in topological order and relaxing their outgoing edges.

The implementation initializes every indegree-zero vertex with distance zero, as required by the assignment. It supports negative edge weights because a DAG cannot contain a directed cycle.

The algorithm also stores parent pointers for path reconstruction.

The time complexity is \(O(V+E)\).

## 3. Theoretical Complexity

| Algorithm | Time Complexity | Auxiliary Space |
|---|---|---|
| DSU operations | \(O(\alpha(V))\) amortized | \(O(V)\) |
| Kruskal | \(O(E\log E)\) | \(O(V+E)\) |
| Prim | \(O(E\log V)\) | \(O(V+E)\) |
| Kosaraju | \(O(V+E)\) | \(O(V+E)\) |
| Kahn | \(O(V+E)\) | \(O(V)\) |
| DAG Shortest Paths | \(O(V+E)\) | \(O(V)\) |

The graph itself is stored using adjacency lists, requiring \(O(V+E)\) memory.

## 4. Experimental Methodology

The benchmark evaluated five graph algorithms on ten configurations.

The vertex counts were 1,000; 3,162; 10,000; 31,623; and 100,000. For each size, two edge densities were tested: \(E=4V\) and \(E=20V\).

Three graph types were generated with a fixed random seed of 42:

- Connected undirected weighted graphs for Kruskal and Prim.
- Directed graphs for Kosaraju.
- Directed acyclic graphs for Kahn and DAG Shortest Paths.

The benchmark recorded execution time, DSU operations, edge scans, comparisons, maximum DFS stack depth, and algorithm results. Execution time was recorded in milliseconds using the median of five runs.

The resulting dataset contains 50 records, stored in `results/results.csv`.

## 5. Experimental Results

### 5.1 Correctness Verification

Kruskal and Prim produced identical minimum spanning tree weights for all ten configurations.

The following table summarizes the expected and observed benchmark results.

| Vertices | Edges | MST Weight | SCC Count | DAG Distance Sum |
|---:|---:|---:|---:|---:|
| 1,000 | 4,000 | 147,970 | 27 | 396,274 |
| 1,000 | 20,000 | 30,363 | 1 | 252,411 |
| 3,162 | 12,648 | 468,911 | 137 | 1,271,911 |
| 3,162 | 63,240 | 97,589 | 1 | 723,157 |
| 10,000 | 40,000 | 1,486,139