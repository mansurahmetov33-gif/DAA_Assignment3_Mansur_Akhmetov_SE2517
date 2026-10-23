
package org.daa.algorithms;

import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagShortestPath {

    public static final long INF = Long.MAX_VALUE / 4;

    public static class Result {
        public final long[] dist;
        public final int[] parent;

        public Result(long[] dist, int[] parent) {
            this.dist = dist;
            this.parent = parent;
        }

        public List<Integer> getPath(int target) {
            if (dist[target] == INF) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();

            for (int v = target; v != -1; v = parent[v]) {
                path.add(v);
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestPaths(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "DAG shortest paths requires a directed graph");
        }

        int n = graph.getN();

        Metrics kahnMetrics = new Metrics();
        Kahn.Result topo = Kahn.topologicalSort(graph, kahnMetrics);

        if (topo.hasCycle) {
            throw new IllegalArgumentException(
                    "Graph contains a cycle");
        }

        int[] indegree = new int[n];

        for (Edge edge : graph.getEdges()) {
            indegree[edge.getTo()]++;
        }

        long[] dist = new long[n];
        int[] parent = new int[n];

        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int v : topo.order) {
            for (Edge edge : graph.getNeighbors(v)) {
                metrics.edgeScans++;

                int to = edge.getTo();
                long newDist = dist[v] + edge.getWeight();

                metrics.comparisons++;

                if (dist[v] != INF && newDist < dist[to]) {
                    dist[to] = newDist;
                    parent[to] = v;
                }
            }
        }

        return new Result(dist, parent);
    }
}
