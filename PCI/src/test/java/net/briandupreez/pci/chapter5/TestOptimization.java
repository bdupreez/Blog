package net.briandupreez.pci.chapter5;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
    public void testPrint(){
        optimization.printSchedule(1, 4, 3, 2, 7, 3, 6, 3, 2, 4, 5, 3);
    }
}
