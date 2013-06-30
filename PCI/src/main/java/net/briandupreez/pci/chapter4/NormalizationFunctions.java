package net.briandupreez.pci.chapter4;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/06/23
 * Time: 6:32 PM
 */
public class NormalizationFunctions {

    /**
     * Normalize values
     *
     * @param value value
     * @param max   max value
     * @param min   min value
     * @return percentage
     */
    public static double percent(final double value, final double max, final double min) {
        return (value - min) / (max - min);
    }


    /**
     * Normalize a value based on a sigmoid function
     *
     * @param value to normalize
     * @return value between 0 and 1
     */
    public static double sigmoid(final double value) {
        return (1 / (1 + Math.pow(Math.E, (-1 * value))));
    }


    /**
     * Normalize the doubles of the input map values.
     * Translated from Programming Collective Intelligence
     * @param input input map
     * @param smallIsBetter if a smaller value is preferred
     * @return input map with normalized values
     */
    public static Map<String, Double> normalizeMap(final Map<String, Double> input, final boolean smallIsBetter) {
        final Map<String, Double> returnMap = new HashMap<>();

        //avoid dvision by 0;
        double vsmall = 0.00001;

        if (smallIsBetter) {
            double minScore = Collections.min(input.values());
            for (final Map.Entry<String, Double> entry : input.entrySet()) {
                final double normed = minScore / Math.max(vsmall, entry.getValue());
                returnMap.put(entry.getKey(), normed);
            }
        } else {
            double maxScore = Collections.max(input.values());
            if (maxScore == 0) {
                maxScore=vsmall;
            }
            for (final Map.Entry<String, Double> entry : input.entrySet()) {
                final double normed = entry.getValue() / maxScore;
                returnMap.put(entry.getKey(), normed);
            }
        }

        return returnMap;
    }


}
