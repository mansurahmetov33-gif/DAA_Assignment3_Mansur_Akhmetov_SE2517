
package org.daa;

import org.daa.graph.Graph;
import org.daa.graph.GraphReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GraphReaderTest {

    @Test
    void testConnectedGraph() throws Exception {
        Graph graph = GraphReader.readFromFile(
                Path.of("data/connected.txt").toString()
        );

        assertEquals(5, graph.getN());
        assertFalse(graph.isDirected());
        assertEquals(6, graph.getEdges().size());
        assertEquals(2, graph.getNeighbors(0).size());
    }

    @Test
    void testDisconnectedGraph() throws Exception {
        Graph graph = GraphReader.readFromFile(
                Path.of("data/disconnected.txt").toString()
        );

        assertEquals(6, graph.getN());
        assertFalse(graph.isDirected());
        assertEquals(3, graph.getEdges().size());

        // Vertex 5 is isolated
        assertTrue(graph.getNeighbors(5).isEmpty());
    }

    @Test
    void testDirectedCyclicGraph() throws Exception {
        Graph graph = GraphReader.readFromFile(
                Path.of("data/cyclic.txt").toString()
        );

        assertEquals(5, graph.getN());
        assertTrue(graph.isDirected());
        assertEquals(6, graph.getEdges().size());

        assertEquals(1, graph.getNeighbors(0).size());
    }
}
