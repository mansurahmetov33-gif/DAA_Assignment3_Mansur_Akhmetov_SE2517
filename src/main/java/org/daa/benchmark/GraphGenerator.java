
package org.daa.benchmark;

import org.daa.graph.Graph;

import java.util.Random;

public class GraphGenerator {

    private static final int SEED = 42;

    private static int[] shuffled(int n, Random rnd) {
        int[] perm = new int[n];

        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);

            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }

        return perm;
    }

    public static Graph undirected(int n, int m) {
        if (n < 1 || m < n - 1) {
            throw new IllegalArgumentException(
                    "Need at least n-1 edges");
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, false);

        int[] perm = shuffled(n, rnd);

        // First create a random spanning tree
        for (int i = 1; i < n; i++) {
            int a = perm[rnd.nextInt(i)];
            int b = perm[i];
            int weight = 1 + rnd.nextInt(1000);

            graph.addEdge(a, b, weight);
        }

        // Add remaining random edges
        for (int i = n - 1; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);

            while (a == b) {
                b = rnd.nextInt(n);
            }

            int weight = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }

    public static Graph directed(int n, int m) {
        if (n < 2 || m < 0) {
            throw new IllegalArgumentException("Invalid n or m");
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, true);

        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);

            while (a == b) {
                b = rnd.nextInt(n);
            }

            int weight = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }

    public static Graph dag(int n, int m) {
        if (n < 2 || m < 0) {
            throw new IllegalArgumentException("Invalid n or m");
        }

        Random rnd = new Random(SEED);
        Graph graph = new Graph(n, true);

        int[] rank = shuffled(n, rnd);

        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);

            while (a == b) {
                b = rnd.nextInt(n);
            }

            if (rank[a] > rank[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            int weight = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }
}
