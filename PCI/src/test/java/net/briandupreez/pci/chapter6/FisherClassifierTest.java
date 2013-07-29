package net.briandupreez.pci.chapter6;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/29
 * Time: 8:29 PM
 */
public class FisherClassifierTest {

    private FisherClassifier fisherClassifier;

    @Before
     public void setup(){
        fisherClassifier = new FisherClassifier(true);
     }

     @After
     public void after(){
         if(fisherClassifier != null){
             fisherClassifier.closeConnection();
         }
     }


    @Test
    public void testCategoryProb() throws Exception {
        Assert.assertEquals(0.5714285714285715, fisherClassifier.probability("quick", "good"));
        Assert.assertEquals(1.0, fisherClassifier.probability("money", "bad"));

    }

    @Test
    public void testFisherProb() throws Exception {
        Assert.assertEquals(0.78013986588958, fisherClassifier.fisherProb("quick rabbit", "good"));

    }
}
