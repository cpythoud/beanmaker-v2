package org.beanmaker.v2.util;

/**
 * Simple class to represent a pair of objects of arbitrary type. Useful when a function should return two values,
 * like a collection and some information about the collection that can only be easily gathered during the
 * collection creation. This class is immutable.
 */
public class Pair<E1, E2> {

    /**
     * The first pair element.
     */
    public final E1 e1;
    /**
     * The second pair element.
     */
    public final E2 e2;

    /**
     * Constructor to initialize immutable pair.
     * @param e1 first pair element
     * @param e2 second pair element
     */
    public Pair(final E1 e1, final E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Returns an hashcode composed with the hashcodes of the two elements of the pair.
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int result = e1.hashCode();
        result = 31 * result + e2.hashCode();
        return result;
    }

    /**
     * Using Object.equals() or an overloaded version, compares two pairs and return true
     * if the first element of the first pair equals the first element of the second pair
     * and the second element of the first pair equals the second element of the second pair.
     * @param obj to be compared
     * @return true if the pair are equals per the definition above, or else false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Pair))
            return false;

        Pair pair = (Pair) obj;
        return e1.equals(pair.e1) && e2.equals(pair.e2);
    }
}
