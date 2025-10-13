package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdatePairsSanitizerTest {

    @Test
    public void testTwoNewValues() {
        var sanitizer = new UpdatePairsSanitizer<String>();

        assertTrue(sanitizer.processCandidates("A", "B"));
        assertTrue(sanitizer.processCandidates("A", "B"));
        assertFalse(sanitizer.processCandidates("A", "C"));

        var pairs = sanitizer.getUpdatablePairs();
        assertTrue(pairs.isEmpty());
        pairs = sanitizer.getReversedPairs();
        assertTrue(pairs.isEmpty());

        var rejects = sanitizer.getExcludedOldValues();
        assertTrue(rejects.contains("A"));
        assertFalse(rejects.contains("B"));
        assertFalse(rejects.contains("C"));
        rejects = sanitizer.getExcludedNewValues();
        assertFalse(rejects.contains("A"));
        assertTrue(rejects.contains("B"));
        assertTrue(rejects.contains("C"));
    }

    @Test
    public void testTwoOldValues() {
        var sanitizer = new UpdatePairsSanitizer<String>();

        assertTrue(sanitizer.processCandidates("A", "B"));
        assertTrue(sanitizer.processCandidates("A", "B"));
        assertTrue(sanitizer.processCandidates("C", "C"));
        assertFalse(sanitizer.processCandidates("D", "B"));
        assertTrue(sanitizer.processCandidates("E", "F"));

        var pairs = sanitizer.getUpdatablePairs();
        assertEquals("C", pairs.get("C"));
        assertEquals("F", pairs.get("E"));
        assertEquals(2, pairs.size());
        pairs = sanitizer.getReversedPairs();
        assertEquals("C", pairs.get("C"));
        assertEquals("E", pairs.get("F"));
        assertEquals(2, pairs.size());

        var rejects = sanitizer.getExcludedOldValues();
        assertTrue(rejects.contains("A"));
        assertFalse(rejects.contains("B"));
        assertFalse(rejects.contains("C"));
        assertTrue(rejects.contains("D"));
        assertFalse(rejects.contains("E"));
        assertFalse(rejects.contains("F"));
        rejects = sanitizer.getExcludedNewValues();
        assertFalse(rejects.contains("A"));
        assertTrue(rejects.contains("B"));
        assertFalse(rejects.contains("C"));
        assertFalse(rejects.contains("D"));
        assertFalse(rejects.contains("E"));
        assertFalse(rejects.contains("F"));
    }

    @Test
    public void testIdenticalValuesSuccess() {
        var sanitizer = new UpdatePairsSanitizer<String>();

        assertTrue(sanitizer.processCandidates("A", "B"));
        assertTrue(sanitizer.processCandidates("A", "B"));
        assertTrue(sanitizer.processCandidates("C", "C"));
        assertFalse(sanitizer.processCandidates("D", "B"));
        assertTrue(sanitizer.processCandidates("E", "F"));

        var pairs = sanitizer.getUpdatableNonIdenticalPairs();
        assertNull(pairs.get("C"));
        assertEquals("F", pairs.get("E"));
        assertEquals(1, pairs.size());
        pairs = sanitizer.getReversedNonIdenticalPairs();
        assertNull(pairs.get("C"));
        assertEquals("E", pairs.get("F"));
        assertEquals(1, pairs.size());

        var rejects = sanitizer.getExcludedOldValues();
        assertTrue(rejects.contains("A"));
        assertFalse(rejects.contains("B"));
        assertFalse(rejects.contains("C"));
        assertTrue(rejects.contains("D"));
        assertFalse(rejects.contains("E"));
        assertFalse(rejects.contains("F"));
        rejects = sanitizer.getExcludedNewValues();
        assertFalse(rejects.contains("A"));
        assertTrue(rejects.contains("B"));
        assertFalse(rejects.contains("C"));
        assertFalse(rejects.contains("D"));
        assertFalse(rejects.contains("E"));
        assertFalse(rejects.contains("F"));
    }

}
