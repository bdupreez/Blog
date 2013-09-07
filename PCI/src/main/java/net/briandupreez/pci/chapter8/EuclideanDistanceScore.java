
package net.briandupreez.pci.chapter8;

import java.util.List;

/**
 * EuclideanDistanceScore.
 * User: bdupreez
 * Date: 2013/04/28
 * Time: 4:27 PM
 */
public class EuclideanDistanceScore {


    /**
     * Determine distance between list of points.
     *
     * @param list1 first list
     * @param list2 second list
     * @return distance between the two lists between 0 and 1... 0 being identical.
     */
    public static double distanceList(final List<Double> list1, final List<Double> list2) {
        if (list1.size() != list2.size()) {
            throw new RuntimeException("Same number of values required.");
        }

        double sumOfAllSquares = 0;
        for (int i = 0; i < list1.size(); i++) {
            sumOfAllSquares += Math.pow(list2.get(i) - list1.get(i), 2);
        }
        return Math.sqrt(sumOfAllSquares);
    }
}
