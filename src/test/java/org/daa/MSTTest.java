
package org.daa;

import org.daa.algorithms.Kruskal;
import org.daa.algorithms.Prim;
import org.daa.benchmark.GraphGenerator;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void testSimpleGraph() {
        Graph graph = new Graph(4, false);

        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 2);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 3);

        Kruskal.Result kruskal =
                Kruskal.findMST(graph, new Metrics());

        Prim.Result prim =
                Prim.findMST(graph, new Metrics());

        assertEquals(6, kruskal.totalWeight);
        assertEquals(6, prim.totalWeight);

        assertEquals(3, kruskal.mstEdges.size());
        assertEquals(3, prim.mstEdges.size());

        assertEquals(1, kruskal.components);
        assertEquals(1, prim.components);
    }

    @Test
    void testDisconnectedGraph() {
        Graph graph = new Graph(5, false);

        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(3, 4, 4);

        Kruskal.Result kruskal =
                Kruskal.findMST(graph, new Metrics());

        Prim.Result prim =
                Prim.findMST(graph, new Metrics());

        assertEquals(9, kruskal.totalWeight);
        assertEquals(9, prim.totalWeight);

        assertEquals(2, kruskal.components);
        assertEquals(2, prim.components);

        assertEquals(3, kruskal.mstEdges.size());
        assertEquals(3, prim.mstEdges.size());
    }

    @Test
    void testSelfLoopsAndParallelEdges() {
        Graph graph = new Graph(3, false);

        graph.addEdge(0, 0, 1);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);

        Kruskal.Result kruskal =
                Kruskal.findMST(graph, new Metrics());

        Prim.Result prim =
                Prim.findMST(graph, new Metrics());

        assertEquals(5, kruskal.totalWeight);
        assertEquals(5, prim.totalWeight);

        assertEquals(2, kruskal.mstEdges.size());
        assertEquals(2, prim.mstEdges.size());
    }

    @Test
    void testOneHundredRandomConnectedGraphs() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {

            int n = 2 + random.nextInt(99);
            int m = n - 1 + random.nextInt(3 * n + 1);

            Graph graph = GraphGenerator.undirected(n, m);

            Kruskal.Result kruskal =
                    Kruskal.findMST(graph, new Metrics());

            Prim.Result prim =
                    Prim.findMST(graph, new Metrics());

            assertEquals(
                    kruskal.totalWeight,
                    prim.totalWeight,
                    "MST weights differ on test " + test
            );

            assertEquals(n - 1, kruskal.mstEdges.size());
            assertEquals(n - 1, prim.mstEdges.size());

            assertEquals(1, kruskal.components);
            assertEquals(1, prim.components);
        }
    }
}
