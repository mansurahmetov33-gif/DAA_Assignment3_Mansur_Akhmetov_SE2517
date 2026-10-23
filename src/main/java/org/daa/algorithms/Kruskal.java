
package org.daa.algorithms;

import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Kruskal {

    public static class Result {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;

        public Result(long totalWeight, List<Edge> mstEdges,
                      int components) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
        }
    }


    public static Result findMST(Graph graph, Metrics metrics) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph");
        }

        List<Edge> edges = new ArrayList<>(graph.getEdges());

        edges.sort((a, b) -> {
            metrics.comparisons++;
            return Integer.compare(a.getWeight(), b.getWeight());
        });

        DisjointSetUnion dsu = new DisjointSetUnion(graph.getN());

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;

        for (Edge edge : edges) {
            metrics.edgeScans++;

            if (dsu.union(edge.getFrom(), edge.getTo())) {
                mstEdges.add(edge);
                totalWeight += edge.getWeight();
            }
        }

        metrics.dsuOps =
                dsu.getFindCalls() + dsu.getSuccessfulUnions();

        return new Result(
                totalWeight,
                mstEdges,
                dsu.components()
        );
    }

}
