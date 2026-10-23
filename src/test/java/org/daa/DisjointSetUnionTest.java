
package org.daa;

import org.daa.algorithms.DisjointSetUnion;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DisjointSetUnionTest {

    @Test
    void testInitialComponents() {
        DisjointSetUnion dsu = new DisjointSetUnion(5);

        assertEquals(5, dsu.components());

        for (int i = 0; i < 5; i++) {
            assertEquals(i, dsu.find(i));
        }
    }

    @Test
    void testUnionAndFind() {
        DisjointSetUnion dsu = new DisjointSetUnion(5);

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));

        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));

        assertEquals(3, dsu.components());
    }

    @Test
    void testDuplicateUnion() {
        DisjointSetUnion dsu = new DisjointSetUnion(4);

        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1));

        assertEquals(3, dsu.components());
        assertEquals(1, dsu.getSuccessfulUnions());
    }

    @Test
    void testRandomUnionsAgainstReference() {
        int n = 100;
        DisjointSetUnion dsu = new DisjointSetUnion(n);

        int[] reference = new int[n];

        for (int i = 0; i < n; i++) {
            reference[i] = i;
        }

        Random random = new Random(42);

        for (int step = 0; step < 500; step++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            int oldA = reference[a];
            int oldB = reference[b];

            dsu.union(a, b);

            if (oldA != oldB) {
                for (int i = 0; i < n; i++) {
                    if (reference[i] == oldB) {
                        reference[i] = oldA;
                    }
                }
            }

            int components = 0;

            for (int i = 0; i < n; i++) {
                boolean firstOccurrence = true;

                for (int j = 0; j < i; j++) {
                    if (reference[j] == reference[i]) {
                        firstOccurrence = false;
                        break;
                    }
                }

                if (firstOccurrence) {
                    components++;
                }
            }

            assertEquals(components, dsu.components());

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    assertEquals(
                            reference[i] == reference[j],
                            dsu.find(i) == dsu.find(j)
                    );
                }
            }
        }
    }
}
