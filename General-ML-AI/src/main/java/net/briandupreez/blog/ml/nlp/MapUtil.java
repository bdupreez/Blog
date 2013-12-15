package net.briandupreez.blog.ml.nlp;

import java.util.*;


public class MapUtil {

    /**
     * Sort a map based on values.
     * The values must be Comparable.
     *
     * @param map       the map to be sorted
     * @param ascending in ascending order, or descending if false
     * @param <K>       key generic
     * @param <V>       value generic
     * @return sorted list
     */
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> entriesSortedByValues(final Map<K, V> map, final boolean ascending) {

        final List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());
        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K, V>>() {
                    @Override
                    public int compare(final Map.Entry<K, V> e1, final Map.Entry<K, V> e2) {
                        if (ascending) {
                            return e1.getValue().compareTo(e2.getValue());
                        } else {
                            return e2.getValue().compareTo(e1.getValue());
                        }
                    }
                }
        );

        return sortedEntries;
    }

}
