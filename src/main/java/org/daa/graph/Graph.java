
package org.daa.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative vertex count");
        }

        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IllegalArgumentException("Invalid vertex");
        }

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adj.get(from).add(edge);

        if (!directed && from != to) {
            adj.get(to).add(new Edge(to, from, weight));
        }
    }

    public int getN() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> getNeighbors(int vertex) {
        return adj.get(vertex);
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
