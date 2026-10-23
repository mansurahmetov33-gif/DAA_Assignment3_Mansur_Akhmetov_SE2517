
package org.daa.graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class GraphReader {

    public static Graph readFromFile(String filename)
            throws IOException {

        try (Scanner scanner = new Scanner(
                Files.newBufferedReader(Path.of(filename)))) {

            if (!scanner.hasNextInt()) {
                throw new IOException("Missing vertex count");
            }
            int n = scanner.nextInt();

            if (!scanner.hasNextInt()) {
                throw new IOException("Missing edge count");
            }
            int m = scanner.nextInt();

            if (!scanner.hasNext()) {
                throw new IOException("Missing graph type");
            }
            String type = scanner.next();

            if (n < 0 || m < 0) {
                throw new IOException("Negative n or m");
            }

            if (!type.equals("directed")
                    && !type.equals("undirected")) {
                throw new IOException("Invalid graph type: " + type);
            }

            boolean directed = type.equals("directed");

            Graph graph = new Graph(n, directed);

            for (int i = 0; i < m; i++) {
                if (!scanner.hasNextInt()) {
                    throw new IOException(
                            "Missing from vertex for edge " + i);
                }
                int from = scanner.nextInt();

                if (!scanner.hasNextInt()) {
                    throw new IOException(
                            "Missing to vertex for edge " + i);
                }
                int to = scanner.nextInt();

                if (!scanner.hasNextInt()) {
                    throw new IOException(
                            "Missing weight for edge " + i);
                }
                int weight = scanner.nextInt();

                if (from < 0 || from >= n
                        || to < 0 || to >= n) {
                    throw new IOException(
                            "Invalid vertex in edge " + i);
                }

                graph.addEdge(from, to, weight);
            }

            if (scanner.hasNext()) {
                throw new IOException(
                        "Extra data after " + m + " edges");
            }

            return graph;
        }
    }
}
