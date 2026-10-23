
package org.daa;

import org.daa.algorithms.*;
import org.daa.graph.Graph;
import org.daa.graph.GraphReader;
import org.daa.metrics.Metrics;

public class Main {

    public static void main(String[] args) throws Exception {

        Graph connected =
                GraphReader.readFromFile("data/connected.txt");

        Graph disconnected =
                GraphReader.readFromFile("data/disconnected.txt");

        Graph cyclic =
                GraphReader.readFromFile("data/cyclic.txt");

        Kruskal.Result mst =
                Kruskal.findMST(connected, new Metrics());

        Prim.Result forest =
                Prim.findMST(disconnected, new Metrics());

        Kosaraju.Result scc =
                Kosaraju.findSCC(cyclic, new Metrics());

        Kahn.Result topo =
                Kahn.topologicalSort(cyclic, new Metrics());

        System.out.println("Connected MST weight: "
                + mst.totalWeight);

        System.out.println("Disconnected forest weight: "
                + forest.totalWeight);

        System.out.println("Disconnected components: "
                + forest.components);

        System.out.println("Cyclic graph SCC count: "
                + scc.sccCount);

        System.out.println("Cycle detected: "
                + topo.hasCycle);
    }
}
