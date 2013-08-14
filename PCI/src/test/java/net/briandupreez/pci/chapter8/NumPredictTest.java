package net.briandupreez.pci.chapter8;

import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/08/12
 * Time: 10:10 AM
 */
public class NumPredictTest {
    final NumPredict numPredict = new NumPredict();
    List<Map<String, List<Double>>> wineSet1;
    List<Map<String, List<Double>>> wineSet2;

    @Before
    public void setup() {
        wineSet1 = numPredict.createWineSet1();
        wineSet2 = numPredict.createWineSet2();
    }

    @Test
    public void testCreateWineSet1() throws Exception {

        Assert.assertTrue(wineSet1.size() == 300);
    }

    @Test
    public void testKnnEstimate() throws Exception {

        final List<Double> list = new ArrayList<>();
        list.add(95.0);
        list.add(3.0);
        System.out.println("Estimated: " + numPredict.knnEstimate(wineSet1, list, 3));

    }

    @Test
    public void testWeightedKnnEstimate() throws Exception {

        final List<Double> list = new LinkedList<>();
        list.add(95.0);
        list.add(3.0);
        System.out.println("Weighted: " + numPredict.weightedKnn(wineSet1, list, 3));

    }

    @Test
    public void testGaussianWeight() {
        final double weight = numPredict.guassianWeight(10, 5);
        Assert.assertEquals(0.1353352832366127, weight);
    }


    @Test
    public void testDivideData() {
        Pair<List, List> pair = numPredict.divideData(wineSet1, 0.1);
        Assert.assertTrue(pair.getValue1().size() < 40);
    }

    @Test
    public void testValidate() {
        double error = numPredict.crossValidate(wineSet1, 0.1, 100);
        System.out.println("Error rate: " + error);
    }


    @Test
    public void testValidateNewNoScale() {
        double error = numPredict.crossValidate(wineSet2, 0.1, 100);
        System.out.println("Error rate, No Scale: " + error);
    }

    @Test
    public void testValidateNewScaled() {
        final List<Double> scales = new LinkedList<>();
        scales.add(10.0);
        scales.add(10.0);
        scales.add(0.0);
        scales.add(0.5);
        final List<Map<String, List<Double>>> rescaled = numPredict.rescale(wineSet2, scales);
        double error = numPredict.crossValidate(rescaled, 0.1, 100);
        System.out.println("Error rate, Scale: " + error);
    }

    /**
     * Takes a while to run
     */
    @Test
    @Ignore
    public void testOptimization() {
        final Optimization optimization = new Optimization();
        final Double[] results = optimization.simulatedAnnealing(optimization.createDomain(), 100.0, 0.9, 2);
        //Assert.assertTrue(5285.0 > cost);
        System.out.println("Annealing results: " + Arrays.toString(results));
    }

    @Test
    public void testGeneric(){
        final Optimization optimization = new Optimization();
        final Double[] results = optimization.geneticAlgorithm(optimization.createDomain(), 50, 1, 0.2, 50, 0.2);
        System.out.println("Genetic Results: " + Arrays.toString(results));
    }

    @Test
    public void testProbability(){
        final List<Double> list = new LinkedList<>();
                list.add(95.0);
                list.add(3.0);
        final double v = numPredict.probabilityGuess(wineSet1, list, 5, 70, 500);
        Assert.assertTrue(v > 0);

    }

}
