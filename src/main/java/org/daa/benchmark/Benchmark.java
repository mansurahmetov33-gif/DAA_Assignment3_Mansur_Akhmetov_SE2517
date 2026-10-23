
package org.daa.benchmark;

import org.daa.algorithms.*;
import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

public class Benchmark {

    private static final int[] SIZES = {
            1000, 3162, 10000, 31623, 100000
    };

    private static final int[] DENSITIES = {4, 20};

    private static final long[] MST_REFERENCE = {
            147970, 30363,
            468911, 97589,
            1486139, 309550,
            4715515, 968517,
            15012094, 3056681
    };

    private static final int[] SCC_REFERENCE = {
            27, 1,
            137, 1,
            373, 1,
            1188, 1,
            3923, 1
    };

    private static final long[] DAG_REFERENCE = {
            396274, 252411,
            1271911, 723157,
            3941828, 2381342,
            12551891, 7376612,
            40096539, 23512087
    };

    private static final int RUNS = 5;

    @FunctionalInterface
    interface AlgorithmRun {
        long execute(Graph graph, Metrics metrics);
    }

    private static void check(
            boolean condition, String message) {

        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void runAlgorithm(
            BufferedWriter writer,
            String algorithm,
            String graphType,
            Graph graph,
            AlgorithmRun action) throws IOException {

        long[] times = new long[RUNS];
        long result = 0;
        Metrics recordedMetrics = null;

        for (int i = 0; i < RUNS; i++) {
            Metrics metrics = new Metrics();

            long start = System.nanoTime();
            long currentResult = action.execute(graph, metrics);
            long end = System.nanoTime();

            times[i] = end - start;

            if (i == 0) {
                result = currentResult;
                recordedMetrics = metrics;
            } else {
                check(result == currentResult,
                        algorithm + " returned inconsistent results");
            }
        }

        Arrays.sort(times);

        double medianMs = times[RUNS / 2] / 1_000_000.0;

        Metrics m = recordedMetrics;

        writer.write(String.format(
                java.util.Locale.US,
                "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n",
                algorithm,
                graphType,
                graph.getN(),
                graph.getEdges().size(),
                medianMs,
                m.dsuOps,
                m.edgeScans,
                m.comparisons,
                m.maxDepth,
                result
        ));

        System.out.printf(
                java.util.Locale.US,
                "%-17s n=%-7d m=%-9d result=%-12d time=%.3f ms%n",
                algorithm,
                graph.getN(),
                graph.getEdges().size(),
                result,
                medianMs
        );
    }

    private static void validateTopologicalOrder(
            Graph graph, Kahn.Result result) {

        check(!result.hasCycle, "DAG unexpectedly has a cycle");
        check(result.order.size() == graph.getN(),
                "Incomplete topological order");

        int[] position = new int[graph.getN()];

        for (int i = 0; i < result.order.size(); i++) {
            position[result.order.get(i)] = i;
        }

        for (Edge edge : graph.getEdges()) {
            check(
                    position[edge.getFrom()] <
                            position[edge.getTo()],
                    "Invalid topological order"
            );
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());

        try (BufferedWriter writer =
                     Files.newBufferedWriter(output)) {

            writer.write(
                    "algorithm,graph,n,m,time_ms,dsu_ops," +
                            "edge_scans,comparisons,max_depth,result\n"
            );

            int index = 0;

            for (int n : SIZES) {
                for (int density : DENSITIES) {

                    int edges = n * density;

                    System.out.println(
                            "\n=== n=" + n + ", m=" + edges + " ==="
                    );

                    // 1. Undirected graph: MST
                    Graph mstGraph =
                            GraphGenerator.undirected(n, edges);

                    long kruskalWeight = Kruskal.findMST(
                            mstGraph, new Metrics()).totalWeight;

                    long primWeight = Prim.findMST(
                            mstGraph, new Metrics()).totalWeight;

                    check(kruskalWeight == primWeight,
                            "Kruskal and Prim disagree!");

                    check(kruskalWeight == MST_REFERENCE[index],
                            "Wrong MST reference value!");

                    runAlgorithm(
                            writer, "Kruskal", "undirected",
                            mstGraph,
                            (g, m) -> Kruskal.findMST(g, m).totalWeight
                    );

                    runAlgorithm(
                            writer, "Prim", "undirected",
                            mstGraph,
                            (g, m) -> Prim.findMST(g, m).totalWeight
                    );

                    // Release MST graph before creating the next
                    mstGraph = null;

                    // 2. Directed graph: SCC
                    Graph directedGraph =
                            GraphGenerator.directed(n, edges);

                    int sccCount = Kosaraju.findSCC(
                            directedGraph, new Metrics()).sccCount;

                    check(sccCount == SCC_REFERENCE[index],
                            "Wrong SCC reference value!");

                    runAlgorithm(
                            writer, "Kosaraju", "directed",
                            directedGraph,
                            (g, m) -> Kosaraju.findSCC(g, m).sccCount
                    );

                    directedGraph = null;

                    // 3. DAG: topological order + shortest paths
                    Graph dagGraph =
                            GraphGenerator.dag(n, edges);

                    Kahn.Result topo = Kahn.topologicalSort(
                            dagGraph, new Metrics());

                    validateTopologicalOrder(dagGraph, topo);

                    long distanceSum = Arrays.stream(
                            DagShortestPath.shortestPaths(
                                    dagGraph, new Metrics()).dist
                    ).sum();

                    check(distanceSum == DAG_REFERENCE[index],
                            "Wrong DAG distance reference value!");

                    runAlgorithm(
                            writer, "Kahn", "dag",
                            dagGraph,
                            (g, m) -> {
                                Kahn.Result r =
                                        Kahn.topologicalSort(g, m);
                                check(!r.hasCycle,
                                        "Cycle detected in DAG");
                                return r.order.size();
                            }
                    );

                    runAlgorithm(
                            writer, "DagShortestPath", "dag",
                            dagGraph,
                            (g, m) -> Arrays.stream(
                                    DagShortestPath.shortestPaths(
                                            g, m).dist
                            ).sum()
                    );

                    dagGraph = null;
                    index++;

                    writer.flush();
                }
            }
        }

        System.out.println(
                "\nBenchmark completed! Results saved to results/results.csv"
        );


        System.out.println("Generating Python plots...");

        ProcessBuilder processBuilder =
                new ProcessBuilder("py", "plot_results.py");

        processBuilder.inheritIO();

        int exitCode = processBuilder.start().waitFor();

        if (exitCode == 0) {
            System.out.println("Plots generated successfully!");
        } else {
            System.err.println("Plot generation failed: " + exitCode);
        }
    }
}
