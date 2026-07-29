package org.dromara.mpe.bind;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionSplitterTest {

    @Test
    void splitList_normalSplit() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<List<Integer>> result = CollectionSplitter.splitList(list, 2);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2), result.get(0));
        assertEquals(Arrays.asList(3, 4), result.get(1));
        assertEquals(Arrays.asList(5), result.get(2));
    }

    @Test
    void splitList_exactDivide() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        List<List<Integer>> result = CollectionSplitter.splitList(list, 2);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList(1, 2), result.get(0));
        assertEquals(Arrays.asList(3, 4), result.get(1));
    }

    @Test
    void splitList_singleElement() {
        List<Integer> list = Collections.singletonList(1);
        List<List<Integer>> result = CollectionSplitter.splitList(list, 5);
        assertEquals(1, result.size());
        assertEquals(Arrays.asList(1), result.get(0));
    }

    @Test
    void splitList_maxLengthLargerThanSize() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<List<Integer>> result = CollectionSplitter.splitList(list, 10);
        assertEquals(1, result.size());
        assertEquals(Arrays.asList(1, 2, 3), result.get(0));
    }

    @Test
    void splitList_maxLengthOne() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        List<List<Integer>> result = CollectionSplitter.splitList(list, 1);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1), result.get(0));
        assertEquals(Arrays.asList(2), result.get(1));
        assertEquals(Arrays.asList(3), result.get(2));
    }

    @Test
    void splitList_nullCollection_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> CollectionSplitter.splitList(null, 2));
    }

    @Test
    void splitList_zeroMaxLength_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> CollectionSplitter.splitList(Arrays.asList(1), 0));
    }

    @Test
    void splitList_negativeMaxLength_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> CollectionSplitter.splitList(Arrays.asList(1), -1));
    }
}
