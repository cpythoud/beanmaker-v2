package org.beanmaker.v2.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This utility class is used to create a map of old values to new values, ensuring that each old value is associated
 * to one and only one new value, and reciprocally that each new value is associated to one and only one old value.
 * <p>
 * This task is achieved by calling the processCandidates method for each pair of old and new values in your
 * data source. The old value will be associated to the new value, if and only if all the conditions below are
 * false:
 * <ul>
 *     <li>old value is already associated to another new value</li>
 *     <li>new value is already associated to another old value</li>
 * </ul>
 * Values that do not satisfy these conditions are stored in Sets that can be retrieved later.
 * <p>
 * WARNINGS: this class is NOT thread-safe; this class should not be used with mutable objects; this class does not
 * preserve the order in which you process elements: if you need sorted data, you will need to process the returned
 * collections accordingly.
 * @param <T> a class with properly implemented <code>toString()</code> and <code>hashCode()</code> functions
 */
public class UpdatePairsSanitizer<T> {

    private final Map<T, T> oldNewPairs = new HashMap<>();
    private final Map<T, T> newOldPairs = new HashMap<>();
    private final Set<T> oldExcludedKeys = new HashSet<>();
    private final Set<T> newExcludedKeys = new HashSet<>();

    /**
     * Processes candidate values for association between existing old values and new values. Determines
     * whether a valid mapping can be established or the candidates must be excluded. Updates the relevant
     * mappings and excluded sets accordingly.
     *
     * @param oldValue the old value to be evaluated for association
     * @param newValue the new value to be evaluated for association
     * @return {@code true} if the association between the old and new values can be successfully
     *         established; {@code false} if the association is invalid and the candidates are excluded
     */
    public boolean processCandidates(T oldValue, T newValue) {
        Objects.requireNonNull(oldValue, "oldValue must not be null");
        Objects.requireNonNull(newValue, "newValue must not be null");

        if (oldExcludedKeys.contains(oldValue) || newExcludedKeys.contains(newValue))
            return false;

        T existingNewValue = oldNewPairs.get(oldValue);
        if (existingNewValue == null) {
            T associatedOldValue = newOldPairs.get(newValue);
            if (associatedOldValue == null) {
                // * The association doesn't exist and both old and new values are not in use
                oldNewPairs.put(oldValue, newValue);
                newOldPairs.put(newValue, oldValue);
                return true;
            }

            if (associatedOldValue.equals(oldValue)) {  // * Should never be the case but we test for it anyway
                // * This is a special failure case that should never present itself as oldNewPairs.get(oldValue)
                // * above should always return newValue and not null in this case.
                throw new AssertionError("old to new value & new to old value maps are not synchronized!"
                        + " Old value: " + oldValue + ", new value: " + newValue);
            }

            // * The new value is already associated with another old value
            oldNewPairs.remove(associatedOldValue);
            newOldPairs.remove(newValue);
            oldExcludedKeys.add(oldValue);
            oldExcludedKeys.add(associatedOldValue);
            newExcludedKeys.add(newValue);
            return false;
        }

        // * if old value is already associated to a new value...
        if (existingNewValue.equals(newValue)) {  // * Check if the association has already been recorded
            if (!newOldPairs.containsKey(newValue) || !newOldPairs.get(newValue).equals(oldValue)) {
                // * Should never be the case but we test for it anyway
                throw new AssertionError("old to new value & new to old value maps are not synchronized!"
                        + " Old value: " + oldValue + ", new value: " + newValue);
            }
            // * The association already exists, nothing to do
            return true;
        }

        // * If we get here it means that the old value is already associated with a different new value
        oldNewPairs.remove(oldValue);
        newOldPairs.remove(existingNewValue);
        oldExcludedKeys.add(oldValue);
        newExcludedKeys.add(newValue);
        newExcludedKeys.add(existingNewValue);
        return false;
    }

    /**
     * Retrieves a map of pairs representing updatable associations between old values and new values.
     * The returned map is an unmodifiable copy of the internally stored mapping.
     *
     * @return an unmodifiable map where each key is an old value and the corresponding value is the associated new value
     */
    public Map<T, T> getUpdatablePairs() {
        return Map.copyOf(oldNewPairs);
    }

    /**
     * Retrieves a map of updatable pairs where the keys and values are non-identical.
     * The returned map contains associations between old values and new values, excluding
     * those where the key equals the value. The map is an unmodifiable copy of the internally
     * stored mapping.
     *
     * @return an unmodifiable map representing updatable associations with non-identical keys and values
     */
    public Map<T, T> getUpdatableNonIdenticalPairs() {
        return Map.copyOf(
                oldNewPairs.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals(entry.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
        );
    }

    /**
     * Retrieves a map of reversed pairs representing associations where keys and values are swapped
     * compared to the original mapping. The returned map is an unmodifiable copy of the internally
     * stored mapping.
     *
     * @return an unmodifiable map where each key is a new value and the corresponding value is
     *         the associated old value
     */
    public Map<T, T> getReversedPairs() {
        return Map.copyOf(newOldPairs);
    }

    /**
     * Retrieves a map of reversed pairs with non-identical keys and values.
     * The returned map contains associations where keys and values are swapped
     * compared to the original mapping and excludes entries where the key equals the value.
     * The map is an unmodifiable copy of the internally stored mapping.
     *
     * @return an unmodifiable map where each key is a new value and the corresponding value is
     *         the associated old value, excluding entries with identical keys and values
     */
    public Map<T, T> getReversedNonIdenticalPairs() {
        return Map.copyOf(
                newOldPairs.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals(entry.getValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
        );
    }

    /**
     * Retrieves an unmodifiable set of values that were excluded from the original old values.
     *
     * @return an unmodifiable set containing the excluded old values
     */
    public Set<T> getExcludedOldValues() {
        return Set.copyOf(oldExcludedKeys);
    }

    /**
     * Retrieves an unmodifiable set of values that were excluded from the new values.
     *
     * @return an unmodifiable set containing the excluded new values
     */
    public Set<T> getExcludedNewValues() {
        return Set.copyOf(newExcludedKeys);
    }

    /**
     * Clears all internal data and allows you to reuse this instance of UpdatePairsSanitizer.
     */
    public void clear() {
        oldNewPairs.clear();
        newOldPairs.clear();
        oldExcludedKeys.clear();
        newExcludedKeys.clear();
    }

}
