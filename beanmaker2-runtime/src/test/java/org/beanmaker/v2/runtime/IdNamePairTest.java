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

        assertEquals("0", pairs.get(0).getIdOrSid());
        assertEquals("2", pairs.get(1).getIdOrSid());
        assertEquals("1", pairs.get(2).getIdOrSid());
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
