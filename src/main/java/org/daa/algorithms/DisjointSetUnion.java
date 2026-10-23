
package org.daa.algorithms;

public class DisjointSetUnion {

    private int[] parent;
    private int[] size;
    private int componentCount;

    private long findCalls;
    private long successfulUnions;

    public DisjointSetUnion(int n) {
        makeSet(n);
    }

    public void makeSet(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative size");
        }

        parent = new int[n];
        size = new int[n];
        componentCount = n;

        findCalls = 0;
        successfulUnions = 0;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        findCalls++;

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (size[rootX] < size[rootY]) {
            int temp = rootX;
            rootX = rootY;
            rootY = temp;
        }

        parent[rootY] = rootX;
        size[rootX] += size[rootY];

        componentCount--;
        successfulUnions++;

        return true;
    }

    public int components() {
        return componentCount;
    }

    public long getFindCalls() {
        return findCalls;
    }

    public long getSuccessfulUnions() {
        return successfulUnions;
    }
}
