package net.briandupreez.pci.chapter9;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/08/17
 * Time: 9:40 PM
 */
public class MatchRow {

    private boolean allNum;
    private int match;
    private double[] ages;
    private String[] data;

    public MatchRow(final String line, final boolean allNum) {

        this.allNum = allNum;
        final String[] split = line.split(",");
        if (allNum) {

            this.ages = new double[split.length - 1];
            for (int i = 0; i < ages.length; i++) {
                this.ages[i] = Double.parseDouble(split[i]);
            }

        }
        this.data = Arrays.copyOfRange(split, 0, split.length - 1);
        match = Integer.parseInt(split[split.length - 1]);
    }

    public MatchRow(final double[] line) {
        this.ages = new double[line.length - 1];
        this.ages = Arrays.copyOfRange(line,0, line.length -1);
        this.allNum = true;
        match = (int) line[line.length - 1];
    }


    public boolean isAllNum() {
        return allNum;
    }

    public int getMatch() {
        return match;
    }

    public double[] getAges() {
        return ages;
    }

    public String[] getData() {
        return data;
    }
}
