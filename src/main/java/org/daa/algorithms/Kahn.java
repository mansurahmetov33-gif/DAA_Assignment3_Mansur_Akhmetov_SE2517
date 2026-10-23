
package org.daa.algorithms;

import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Kahn {

    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;

        public Result(List<Integer> order, boolean hasCycle) {
            this.order = order;
            this.hasCycle = hasCycle;
        }
    }

    public static Result topologicalSort(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kahn requires a directed graph");
        }

        int n = graph.getN();
        int[] indegree = new int[n];

        for (Edge edge : graph.getEdges()) {
            indegree[edge.getTo()]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);

            for (Edge edge : graph.getNeighbors(v)) {
                metrics.edgeScans++;

                int to = edge.getTo();
                indegree[to]--;

                if (indegree[to] == 0) {
                    queue.offer(to);
                }
            }
        }

        boolean hasCycle = order.size() != n;

        return new Result(order, hasCycle);
    }
}
