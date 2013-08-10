package net.briandupreez.pci.chapter7;

import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Test;

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

        final Pair<Object[][], Object[][]> tuple = treePredict.divideSet(treePredict.retrieveDataAsMatrix(), 2, "yes");
        System.out.println(tuple);
        Assert.assertEquals(8, tuple.getValue0().length);
    }

    @Test
    public void testUniqueCounts() throws Exception {
        final Map<String, Integer> stringIntegerMap = treePredict.uniqueCounts(treePredict.retrieveDataAsMatrix());
        System.out.println(stringIntegerMap);
        Assert.assertEquals(3, stringIntegerMap.size());

    }

    @Test
    public void testGini() throws Exception {
        double result = treePredict.calculateGiniImpurity(treePredict.retrieveDataAsMatrix());
        Assert.assertEquals(0.6328125, result);
    }

    @Test
    public void testEntropy() throws Exception {
        double result = treePredict.calculateEntropy(treePredict.retrieveDataAsMatrix());
        Assert.assertEquals(1.5052408149441479, result);
    }

    @Test
    public void testRetrieveDataAsArray() throws Exception {
        final Object[][] objects = treePredict.retrieveDataAsMatrix();
        Assert.assertEquals(16, objects.length);

    }

    @Test
    public void testBuildTree() throws Exception {
       final DecisionNode decisionNode = treePredict.buildTree(treePredict.retrieveDataAsMatrix());
       Assert.assertNotNull(decisionNode);
        treePredict.printTree(decisionNode, " ");
        treePredict.prune(decisionNode, 0.1);
        treePredict.printTree(decisionNode, " ");
    }

    @Test
    public void testClassify() throws Exception {
        final DecisionNode decisionNode = treePredict.buildTree(treePredict.retrieveDataAsMatrix());
        final Map<String, Integer> classify = treePredict.classify(new Object[]{"(direct)", "USA", "yes", 5}, decisionNode);
        System.out.println(classify);
    }

}
