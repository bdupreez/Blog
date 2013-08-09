package net.briandupreez.pci.chapter7;

import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * TreePredictTest.
 * User: bdupreez
 * Date: 2013/08/09
 * Time: 6:38 PM
 */
public class TreePredictTest {

    final TreePredict treePredict = new TreePredict();

    @Test
    public void testDivideSet() throws Exception {

        final Pair<List, List> tuple = treePredict.divideSet("readFAQ", "yes");
        System.out.println(tuple);
        Assert.assertEquals(8, tuple.getValue0().size());
    }

    @Test
    public void testUniqueCounts() throws Exception {
        final Map<String, Integer> stringIntegerMap = treePredict.uniqueCounts();
        System.out.println(stringIntegerMap);
        Assert.assertEquals(3, stringIntegerMap.size());

    }

    @Test
    public void testGini() throws Exception {
        double result = treePredict.calculateGiniImpurity();
        Assert.assertEquals(0.6328125, result);
    }

    @Test
    public void testEntropy() throws Exception {
        double result = treePredict.calculateEntropy();
        Assert.assertEquals(1.5052408149441479, result);
    }

}
