# DAA Assignment 3 — Graph Algorithms

**Student:** Mansur Akhmetov  
**Group:** SE-2517  
**Course:** Design and Analysis of Algorithms  
**Language:** Java 25  
**Build Tool:** Maven

## Project Overview

This project implements and benchmarks fundamental graph algorithms using adjacency-list graph representations.

The assignment includes Disjoint Set Union (DSU), Minimum Spanning Tree algorithms, Strongly Connected Components, topological sorting, and shortest paths in directed acyclic graphs.

The implementation supports directed and undirected weighted graphs, disconnected graphs, self-loops, parallel edges, and isolated vertices.

## Implemented Algorithms

| Algorithm | Description | Time Complexity |
|---|---|---|
| DSU | Path compression and union by size | O(α(V)) amortized |
| Kruskal | Minimum spanning tree or forest using DSU | O(E log E) |
| Prim | Minimum spanning tree or forest using PriorityQueue | O(E log V) |
| Kosaraju | Strongly connected components using iterative DFS | O(V + E) |
| Kahn | Topological sorting and cycle detection | O(V + E) |
| DAG Shortest Path | Shortest paths using topological order | O(V + E) |

## Project Structure

```text
DAA_Assignment3_Mansur_Akhmetov_SE2517/
├── pom.xml
├── README.md
├── REPORT.md
├── plot_results.py
├── data/
│   ├── connected.txt
│   ├── disconnected.txt
│   └── cyclic.txt
├── results/
│   ├── results.csv
│   └── plots/
│       ├── execution_time.png
│       ├── edge_scans.png
│       └── dsu_operations.png
└── src/
    ├── main/java/org/daa/
    │   ├── Main.java
    │   ├── graph/
    │   │   ├── Graph.java
    │   │   ├── Edge.java
    │   │   └── GraphReader.java
    │   ├── algorithms/
    │   │   ├── DisjointSetUnion.java
    │   │   ├── Kruskal.java
    │   │   ├── Prim.java
    │   │   ├── Kosaraju.java
    │   │   ├── Kahn.java
    │   │   └── DagShortestPath.java
    │   ├── metrics/
    │   │   └── Metrics.java
    │   └── benchmark/
    │       ├── GraphGenerator.java
    │       └── Benchmark.java
    └── test/java/org/daa/
        ├── DisjointSetUnionTest.java
        ├── MSTTest.java
        ├── KosarajuTest.java
        ├── KahnTest.java
        ├── DagShortestPathTest.java
        └── GraphReaderTest.java
```

## Requirements

- JDK 25
- Apache Maven
- Python 3
- Python packages: `matplotlib`, `pandas`

JUnit 5 is automatically downloaded through Maven.


Expected output for the provided sample graphs:

```text
Connected MST weight: 8
Disconnected forest weight: 9
Disconnected components: 3
Cyclic graph SCC count: 2
Cycle detected: true
```

## Input File Format

Graph files use the following format:

```text
n m directed|undirected
u v w
u v w
...
```

Where:

- `n` is the number of vertices.
- `m` is the number of edges.
- `directed` or `undirected` specifies graph type.
- `u` and `v` are zero-based vertex indices.
- `w` is the integer edge weight.

Example:

```text
3 3 undirected
0 1 5
1 2 10
0 2 3
```

Sample graphs are available in the `data/` directory.

## Unit Testing

The project uses JUnit 5.

Run all tests with:

```bash
mvn test
```

The tests cover:

- DSU operations and comparison against a reference implementation.
- Kruskal and Prim agreement on 100 random connected graphs.
- Minimum spanning forests for disconnected graphs.
- Kosaraju SCC correctness compared with brute-force reachability.
- Iterative DFS on a chain of 100,000 vertices.
- Kahn topological ordering and cycle detection.
- DAG shortest paths compared with Bellman-Ford, including negative weights.
- Graph loading from text files.

## Benchmark

The benchmark measures five algorithms:

1. Kruskal
2. Prim
3. Kosaraju
4. Kahn
5. DAG Shortest Path

The tested graph sizes are:

| Vertices | Edges (4V) | Edges (20V) |
|---:|---:|---:|
| 1,000 | 4,000 | 20,000 |
| 3,162 | 12,648 | 63,240 |
| 10,000 | 40,000 | 200,000 |
| 31,623 | 126,492 | 632,460 |
| 100,000 | 400,000 | 2,000,000 |

The graph generator uses a fixed random seed of `42`.

Each configuration is measured five times, and the median execution time is recorded.


## Experimental Results

For the largest configuration of 100,000 vertices and 2,000,000 edges:

| Algorithm | Execution Time (ms) | Result |
|---|---:|---:|
| Kruskal | 500.970 | MST weight: 3,056,681 |
| Prim | 1,292.490 | MST weight: 3,056,681 |
| Kosaraju | 280.712 | SCC count: 1 |
| Kahn | 58.857 | Topological order size: 100,000 |
| DAG Shortest Path | 122.232 | Distance sum: 23,512,087 |

