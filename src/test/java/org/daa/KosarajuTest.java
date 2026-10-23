
package org.daa;

import org.daa.algorithms.Kosaraju;
import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class KosarajuTest {

    @Test
    void testSimpleSCC() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);

        Metrics metrics = new Metrics();

        Kosaraju.Result result =
                Kosaraju.findSCC(graph, metrics);

        assertEquals(2, result.sccCount);

        assertEquals(result.component[0], result.component[1]);
        assertEquals(result.component[1], result.component[2]);

        assertEquals(result.component[3], result.component[4]);

        assertNotEquals(result.component[0], result.component[3]);

        assertEquals(12, metrics.edgeScans);
    }

    @Test
    void testDisconnectedVertices() {
        Graph graph = new Graph(5, true);

        Kosaraju.Result result =
                Kosaraju.findSCC(graph, new Metrics());

        assertEquals(5, result.sccCount);
    }

    @Test
    void testSelfLoop() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 1, 1);

        Kosaraju.Result result =
                Kosaraju.findSCC(graph, new Metrics());

        assertEquals(2, result.sccCount);
        assertEquals(result.component[1], result.component[2]);
        assertNotEquals(result.component[0], result.component[1]);
    }


    private boolean[] reachable(Graph graph, int start) {
        int n = graph.getN();

        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();

            for (Edge edge : graph.getNeighbors(v)) {
                int to = edge.getTo();

                if (!visited[to]) {
                    visited[to] = true;
                    queue.offer(to);
                }
            }
        }

        return visited;
    }

    @Test
    void testRandomGraphsAgainstBruteForce() {
        Random random = new Random(42);

        for (int test = 0; test < 30; test++) {
            int n = 2 + random.nextInt(199);
            int m = random.nextInt(n * 4 + 1);

            Graph graph = new Graph(n, true);

            for (int i = 0; i < m; i++) {
                int from = random.nextInt(n);
                int to = random.nextInt(n);

                graph.addEdge(from, to, 1);
            }

            Kosaraju.Result result =
                    Kosaraju.findSCC(graph, new Metrics());

            boolean[][] reach = new boolean[n][];

            for (int i = 0; i < n; i++) {
                reach[i] = reachable(graph, i);
            }


            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    boolean expected =
                            reach[i][j] && reach[j][i];

                    boolean actual =
                            result.component[i] == result.component[j];

                    assertEquals(
                            expected,
                            actual,
                            "Incorrect SCC for vertices " + i +
                                    " and " + j +
                                    " in test " + test
                    );
                }
            }

            assertEquals(2L * m,
                    newMetricsEdgeScans(graph));
        }
    }

    private long newMetricsEdgeScans(Graph graph) {
        Metrics metrics = new Metrics();
        Kosaraju.findSCC(graph, metrics);
        return metrics.edgeScans;
    }

    @Test
    void testLargeChainNoStackOverflow() {
        int n = 100000;

        Graph graph = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        Metrics metrics = new Metrics();

        Kosaraju.Result result =
                assertDoesNotThrow(
                        () -> Kosaraju.findSCC(graph, metrics)
                );

        assertEquals(n, result.sccCount);
        assertEquals(2L * (n - 1), metrics.edgeScans);

        assertTrue(metrics.maxDepth > 0);
    }
}
