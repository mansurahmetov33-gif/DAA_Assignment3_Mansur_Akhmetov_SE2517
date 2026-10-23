
package org.daa.algorithms;

import org.daa.graph.Edge;
import org.daa.graph.Graph;
import org.daa.metrics.Metrics;

import java.util.*;

public class Kosaraju {

    public static class Result {
        public final int sccCount;
        public final int[] component;

        public Result(int sccCount, int[] component) {
            this.sccCount = sccCount;
            this.component = component;
        }
    }

    private static class Frame {
        int vertex;
        int nextIndex;

        Frame(int vertex) {
            this.vertex = vertex;
            this.nextIndex = 0;
        }
    }

    public static Result findSCC(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kosaraju requires a directed graph");
        }

        int n = graph.getN();

        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>();

        // First DFS: calculate finishing order
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            Deque<Frame> stack = new ArrayDeque<>();
            visited[start] = true;
            stack.push(new Frame(start));

            metrics.maxDepth = Math.max(
                    metrics.maxDepth, stack.size());

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                List<Edge> neighbors =
                        graph.getNeighbors(frame.vertex);

                if (frame.nextIndex < neighbors.size()) {
                    Edge edge = neighbors.get(frame.nextIndex++);
                    metrics.edgeScans++;

                    int to = edge.getTo();

                    if (!visited[to]) {
                        visited[to] = true;
                        stack.push(new Frame(to));

                        metrics.maxDepth = Math.max(
                                metrics.maxDepth, stack.size());
                    }
                } else {
                    finishOrder.add(frame.vertex);
                    stack.pop();
                }
            }
        }

        Graph reversed = new Graph(n, true);

        for (Edge edge : graph.getEdges()) {
            reversed.addEdge(
                    edge.getTo(),
                    edge.getFrom(),
                    edge.getWeight()
            );
        }

        Arrays.fill(visited, false);

        int[] component = new int[n];
        Arrays.fill(component, -1);

        int sccCount = 0;

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int start = finishOrder.get(i);

            if (visited[start]) {
                continue;
            }

            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            visited[start] = true;

            metrics.maxDepth = Math.max(
                    metrics.maxDepth, stack.size());

            while (!stack.isEmpty()) {
                int v = stack.pop();
                component[v] = sccCount;

                for (Edge edge : reversed.getNeighbors(v)) {
                    metrics.edgeScans++;

                    int to = edge.getTo();

                    if (!visited[to]) {
                        visited[to] = true;
                        stack.push(to);

                        metrics.maxDepth = Math.max(
                                metrics.maxDepth, stack.size());
                    }
                }
            }

            sccCount++;
        }

        return new Result(sccCount, component);
    }
}
