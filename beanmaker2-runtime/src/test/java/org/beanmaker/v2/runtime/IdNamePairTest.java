package org.beanmaker.v2.runtime;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdNamePairTest {

    @Test
    public void testINPOrdering() {
        var pairs = new ArrayList<IdNamePair>();
        pairs.add(new IdNamePair(1, "bla bla"));
        pairs.add(new IdNamePair(0, "please select"));
        pairs.add(new IdNamePair(2, "adada"));

        pairs.sort(new IdNamePair.IdNamePairLocalizedComparator(new TestLanguage()));

        assertEquals(pairs.get(0).getId(), "0");
        assertEquals(pairs.get(1).getId(), "2");
        assertEquals(pairs.get(2).getId(), "1");
    }

    private static class TestLanguage implements DbBeanLanguage {

        @Override
        public long getId() {
            return 1;
        }

        @Override
        public String getNameForIdNamePairsAndTitles(DbBeanLanguage language) {
            return "French";
        }

        @Override
        public String getName() {
            return "français";
        }

        @Override
        public String getIso() {
            return "fr";
        }

    }

}
