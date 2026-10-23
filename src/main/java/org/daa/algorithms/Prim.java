
package org.daa.algorithms;

import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

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
                    "Prim requires an undirected graph");
        }

        int n = graph.getN();
        boolean[] visited = new boolean[n];

        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
            metrics.comparisons++;
            return Integer.compare(a.getWeight(), b.getWeight());
        });

        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            components++;
            visited[start] = true;

            for (Edge edge : graph.getNeighbors(start)) {
                metrics.edgeScans++;
                if (!visited[edge.getTo()]) {
                    pq.offer(edge);
                }
            }

            while (!pq.isEmpty()) {
                Edge edge = pq.poll();
                int to = edge.getTo();

                if (visited[to]) {
                    continue;
                }

                visited[to] = true;
                mstEdges.add(edge);
                totalWeight += edge.getWeight();

                for (Edge next : graph.getNeighbors(to)) {
                    metrics.edgeScans++;

                    if (!visited[next.getTo()]) {
                        pq.offer(next);
                    }
                }
            }
        }

        return new Result(totalWeight, mstEdges, components);
    }

}
