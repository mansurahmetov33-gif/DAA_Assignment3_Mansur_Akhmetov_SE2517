
package org.daa.metrics;

public class Metrics {

    public long dsuOps = 0;
    public long edgeScans = 0;
    public long comparisons = 0;
    public long maxDepth = 0;

    public void reset() {
        dsuOps = 0;
        edgeScans = 0;
        comparisons = 0;
        maxDepth = 0;
    }
}
