package org.beanmaker.v2.util;

import java.util.HashSet;
import java.util.Set;

public class Sets {

    public static  <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        return intersection(set1, set2, new HashSet<T>());
    }

    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2, Set<T> intersection) {
        intersection.addAll(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    public static  <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        return difference(set1, set2, new HashSet<T>());
    }

    public static  <T> Set<T> difference(Set<T> set1, Set<T> set2, Set<T> difference) {
        difference.addAll(set1);
        difference.removeAll(set2);
        return difference;
    }

    public <T> Set<T> union(Set<T> set1, Set<T> set2) {
        return union(set1, set2, new HashSet<T>());
    }

    public static  <T> Set<T> union(Set<T> set1, Set<T> set2, Set<T> union) {
        union.addAll(set1);
        union.addAll(set2);
        return union;
    }

}
