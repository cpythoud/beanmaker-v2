package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.beanmaker.v2.util.Files.nameForDuplicatedFile;

public class DuplicateFileTest {

    @Test
    void testRenaming() {
        assertEquals("file (1).txt", nameForDuplicatedFile("file.txt"));
        assertEquals("file (2).txt", nameForDuplicatedFile("file (1).txt"));
        assertEquals("file (13).txt", nameForDuplicatedFile("file (12).txt"));

        assertEquals("file (1)", nameForDuplicatedFile("file"));
        assertEquals("file (2)", nameForDuplicatedFile("file (1)"));
        assertEquals("file (13)", nameForDuplicatedFile("file (12)"));
    }

}
