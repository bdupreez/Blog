package net.briandupreez.pci.chapter9;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/08/17
 * Time: 10:25 PM
 */
public class AdvancedClassifyTest {
    final AdvancedClassify advancedClassify = new AdvancedClassify();
    @Test
    public void testLoadMatch() throws Exception {

        final List<MatchRow> matchRowsAges = advancedClassify.loadMatch("agesonly.csv", true);
        final List<MatchRow> matchRows = advancedClassify.loadMatch("matchmaker.csv", false);
        Assert.assertEquals(500, matchRowsAges.size());
        Assert.assertEquals(500, matchRows.size());

    }

    @Test
    public void testLinear() throws Exception {
        final List<MatchRow> matchRowsAges = advancedClassify.loadMatch("agesonly.csv", true);
        final Map<Integer,Double[]> linear = advancedClassify.linearTrain(matchRowsAges);

        Assert.assertEquals(2, linear.size());

    }

    @Test
    public void testDotProduct() throws  Exception{
        final List<MatchRow> matchRowsAges = advancedClassify.loadMatch("agesonly.csv", true);
        final Map<Integer,Double[]> linear = advancedClassify.linearTrain(matchRowsAges);
        Assert.assertEquals(1,advancedClassify.dotProductClassify(new Double[]{30.0,30.0}, linear));
        Assert.assertEquals(1,advancedClassify.dotProductClassify(new Double[]{30.0,25.0}, linear));
        Assert.assertEquals(0,advancedClassify.dotProductClassify(new Double[]{25.0,40.0}, linear));
        Assert.assertEquals(1,advancedClassify.dotProductClassify(new Double[]{48.0,20.0}, linear));

    }


    @Test
    public void testLoadNumeric(){
        final List<MatchRow> matchRows = advancedClassify.loadNumerical();
        final MatchRow matchRow = matchRows.get(0);
        Assert.assertEquals(1.0, matchRow.getAges()[1]);
    }
}
