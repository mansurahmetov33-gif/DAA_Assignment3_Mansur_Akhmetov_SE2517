
package org.daa;

import org.daa.algorithms.DagShortestPath;
import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, -4);

        Metrics metrics = new Metrics();

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(graph, metrics);

        assertArrayEquals(
                new long[]{0, 2, 5, 1},
                result.dist
        );

        assertEquals(
                List.of(0, 2, 3),
                result.getPath(3)
        );

        assertEquals(4, metrics.edgeScans);
    }

    @Test
    void testMultipleSources() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 2, 10);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 3, 2);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(
                        graph, new Metrics());

        assertArrayEquals(
                new long[]{0, 0, 3, 5},
                result.dist
        );

        assertEquals(List.of(1, 2, 3), result.getPath(3));
    }

    @Test
    void testNegativeWeights() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, -5);
        graph.addEdge(1, 2, -3);
        graph.addEdge(0, 2, 10);

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(
                        graph, new Metrics());

        assertArrayEquals(
                new long[]{0, -5, -8},
                result.dist
        );
    }

    @Test
    void testCycleRejected() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> DagShortestPath.shortestPaths(
                        graph, new Metrics())
        );
    }

    private long[] bellmanFordReference(Graph graph) {
        int n = graph.getN();

        int[] indegree = new int[n];

        for (Edge edge : graph.getEdges()) {
            indegree[edge.getTo()]++;
        }

        long[] dist = new long[n];
        Arrays.fill(dist, DagShortestPath.INF);

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int iteration = 0; iteration < n - 1; iteration++) {
            boolean changed = false;

            for (Edge edge : graph.getEdges()) {
                int u = edge.getFrom();
                int v = edge.getTo();

                if (dist[u] == DagShortestPath.INF) {
                    continue;
                }

                long candidate = dist[u] + edge.getWeight();

                if (candidate < dist[v]) {
                    dist[v] = candidate;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return dist;
    }

    @Test
    void testRandomDAGsAgainstBellmanFord() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(49);
            int m = random.nextInt(n * 5 + 1);

            Graph graph = new Graph(n, true);

            // Edges always go from smaller to larger vertex
            // so a cycle cannot appear
            for (int i = 0; i < m; i++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);

                if (a == b) {
                    continue;
                }

                int from = Math.min(a, b);
                int to = Math.max(a, b);
                int weight = random.nextInt(41) - 20;

                graph.addEdge(from, to, weight);
            }

            long[] expected = bellmanFordReference(graph);

            Metrics metrics = new Metrics();

            DagShortestPath.Result actual =
                    DagShortestPath.shortestPaths(
                            graph, metrics);

            assertArrayEquals(
                    expected,
                    actual.dist,
                    "Incorrect shortest paths in test " + test
            );

            assertEquals(
                    graph.getEdges().size(),
                    metrics.edgeScans
            );
        }
    }
}
