package net.briandupreez.pci.chapter6;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/28
 * Time: 12:47 PM
 */
public class NaiveBayesTest {

    private NaiveBayes naiveBayes;

    @Before
    public void setup(){
        naiveBayes = new NaiveBayes("test.db",true, true);
    }

    @After
    public void after(){
        if(naiveBayes != null){
            naiveBayes.closeConnection();
        }
    }

    @Test
    public void testProbability() throws Exception {

        double prob = naiveBayes.prob("quick rabbit", "good");
        Assert.assertEquals(0.15624999999999997, prob);
    }

    @Test
    public void testClassify() throws Exception {
        String category = naiveBayes.classify("quick rabbit");
        Assert.assertEquals("good", category);
        category = naiveBayes.classify("quick money");
        Assert.assertEquals("bad", category);
        naiveBayes.addThreshold("bad", 3.0);
        category = naiveBayes.classify("quick money");
        Assert.assertEquals("unknown", category);

        naiveBayes = null;

    }
}
