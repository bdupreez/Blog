package net.briandupreez.pci.chapter5;

import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/15
 * Time: 10:26 PM
 */

public class TestOptimization {

    final Optimization optimization = new Optimization();

    @Before
    public void setup() throws IOException {
        optimization.readFlights();
    }

    @Test
    public void testOptimizationCost() throws Exception {

        double cost = optimization.scheduleCost(1, 4, 3, 2, 7, 3, 6, 3, 2, 4, 5, 3);
        Assert.assertEquals(5285.0, cost);
        System.out.println("Cost: " + cost);

    }

    @Test
    public void testOptimizationRandom() throws Exception {
        double cost = optimization.scheduleCost(optimization.randomOptimize());
        Assert.assertTrue(5285.0 > cost);
        System.out.println("Cost: " + cost);

    }

    @Test
    public void testHillClimb() throws Exception {
        final List<Pair<Integer, Integer>> domain = new ArrayList<>(12);
        for(int i = 0; i < 12; i++){
            final Pair<Integer, Integer> pair = new Pair<>(0,9);
           domain.add(pair);
        }
        double cost = optimization.scheduleCost(optimization.hillClimb(domain));
        Assert.assertTrue(5285.0 > cost);
        System.out.println("Cost: " + cost);

    }

    @Test
    public void testSimulatedAnnealing() throws Exception {
        final List<Pair<Integer, Integer>> domain = new ArrayList<>(12);
        for(int i = 0; i < 12; i++){
            final Pair<Integer, Integer> pair = new Pair<>(0,9);
           domain.add(pair);
        }
        double cost = optimization.scheduleCost(optimization.simulatedAnnealing(domain, 80000.0, 0.989, 2));
        Assert.assertTrue(5285.0 > cost);
        System.out.println("Annealing Cost: " + cost);

    }

    @Test
    public void testGenetic() throws Exception {
        final List<Pair<Integer, Integer>> domain = new ArrayList<>(12);
        for(int i = 0; i < 12; i++){
            final Pair<Integer, Integer> pair = new Pair<>(0,9);
           domain.add(pair);
        }
        double cost = optimization.scheduleCost(
                optimization.geneticAlgorithm(domain, 50, 1,0.2, 500, 0.2));
        Assert.assertTrue(5285.0 > cost);
        System.out.println("Genetic Cost: " + cost);

    }

    @Test
    public void testPrint(){
        optimization.printSchedule(1, 4, 3, 2, 7, 3, 6, 3, 2, 4, 5, 3);
    }
}
