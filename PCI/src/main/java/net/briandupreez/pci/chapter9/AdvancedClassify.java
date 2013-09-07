package net.briandupreez.pci.chapter9;

import com.google.common.io.Resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/08/17
 * Time: 9:24 PM
 */
public class AdvancedClassify {


    /**
     * Load the rows
     *
     * @param file   the file name
     * @param allnum does it contain all numbers
     * @return a list of matchrow objects
     */
    public List<MatchRow> loadMatch(final String file, final boolean allnum) {
        final List<MatchRow> rows = new ArrayList<>();

        try (final BufferedReader br = new BufferedReader(new FileReader(Resources.getResource(file).getFile()))) {
            while (br.ready()) {
                final String line = br.readLine();
                rows.add(new MatchRow(line, allnum));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * Linear classifier
     *
     * @param rows the rows
     * @return
     */
    public Map<Integer, Double[]> linearTrain(final List<MatchRow> rows) {

        final Map<Integer, Double[]> averages = new HashMap<>();
        final Map<Integer, Double> counts = new HashMap<>();

        for (final MatchRow row : rows) {
            int cl = row.getMatch();

            if (!averages.containsKey(cl)) {
                averages.put(cl, new Double[]{0.0, 0.0});
            }
            if (!counts.containsKey(cl)) {
                counts.put(cl, 0.0);
            }
            for (int i = 0; i < row.getAges().length; i++) {
                averages.get(cl)[i] += row.getAges()[i];
            }
            counts.put(cl, counts.get(cl) + 1);
        }
        for (final Map.Entry<Integer, Double[]> entry : averages.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                entry.getValue()[i] /= counts.get(entry.getKey());
            }
        }
        return averages;
    }

    /**
     * Calculate the dot product of 2 arrays
     *
     * @param a first array
     * @param b second array
     * @return the dot product
     */
    public double calucalteDotProduct(final Double[] a, final Double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("The leanth of the 2 arrays must be equal.");
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * Classify using dp...
     *
     * @param point
     * @param averages
     * @return
     */
    public int dotProductClassify(final Double[] point, final Map<Integer, Double[]> averages) {
        double b = (calucalteDotProduct(averages.get(1), averages.get(1)) - calucalteDotProduct(averages.get(0), averages.get(0))) / 2;
        double y = calucalteDotProduct(point, averages.get(0)) - calucalteDotProduct(point, averages.get(1)) + b;
        if (y > 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Load the data as numeric.
     * @return new data
     */
    public List<MatchRow> loadNumerical() {
        final List<MatchRow> oldRows = loadMatch("matchmaker.csv", false);
        final List<MatchRow> newRows = new ArrayList<>();
        for (final MatchRow oldRow : oldRows) {
            final List<String> d = Arrays.asList(oldRow.getData());

            double[] data = new double[]{
                    Double.parseDouble(d.get(0)), yesNo(d.get(1)), yesNo(d.get(2)),
                    Double.parseDouble(d.get(5)), yesNo(d.get(6)), yesNo(d.get(7)),
                    matchCount(d.get(3), d.get(8)),
                    determineDistance(),
                    oldRow.getMatch()};
            newRows.add(new MatchRow(data));
        }

        return newRows;
    }

    /**
     * String to 1,0,-1
     *
     * @param value the value
     * @return 1, 0 , -1
     */
    public double yesNo(final String value) {
        if ("yes".equals(value)) {
            return 1;
        } else if ("no".equals(value)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * count the matching items in the strings
     *
     * @param interest1 interest1
     * @param interest2 interest2
     * @return double count
     */
    public double matchCount(final String interest1, final String interest2) {
        final String[] list1 = interest1.split(":");
        final String[] list2 = interest2.split(":");
        double x = 0.0;
        final List<String> s2s = Arrays.asList(list2);
        for (final String s1 : list1) {
            if (s2s.contains(s1)) {
                x += 1.0;
            }
        }
        return x;
    }

    /**
     * Not paying for yahoo searches, just yet :)
     *
     * @return
     */
    public double determineDistance() {
        return 0;
    }
}
