
package org.daa;

import org.daa.algorithms.Kahn;
import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KahnTest {

    private void validateOrder(Graph graph, List<Integer> order) {
        int n = graph.getN();

        assertEquals(n, order.size());

        int[] position = new int[n];
        boolean[] seen = new boolean[n];

        for (int i = 0; i < n; i++) {
            int vertex = order.get(i);

            assertTrue(vertex >= 0 && vertex < n);
            assertFalse(seen[vertex]);

            seen[vertex] = true;
            position[vertex] = i;
        }

        for (Edge edge : graph.getEdges()) {
            assertTrue(
                    position[edge.getFrom()] < position[edge.getTo()],
                    "Invalid topological order"
            );
        }
    }

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new Metrics();

        Kahn.Result result =
                Kahn.topologicalSort(graph, metrics);

        assertFalse(result.hasCycle);
        validateOrder(graph, result.order);

        assertEquals(4, metrics.edgeScans);
    }

    @Test
    void testCycleDetection() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        Kahn.Result result =
                Kahn.topologicalSort(graph, new Metrics());

        assertTrue(result.hasCycle);
        assertTrue(result.order.size() < graph.getN());
    }

    @Test
    void testDisconnectedDAG() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);

        Kahn.Result result =
                Kahn.topologicalSort(graph, new Metrics());

        assertFalse(result.hasCycle);
        validateOrder(graph, result.order);
    }

    @Test
    void testSelfLoop() {
        Graph graph = new Graph(2, true);

        graph.addEdge(0, 0, 1);

        Kahn.Result result =
                Kahn.topologicalSort(graph, new Metrics());

        assertTrue(result.hasCycle);
    }

    @Test
    void testEmptyGraph() {
        Graph graph = new Graph(0, true);

        Kahn.Result result =
                Kahn.topologicalSort(graph, new Metrics());

        assertFalse(result.hasCycle);
        assertTrue(result.order.isEmpty());
    }
}
