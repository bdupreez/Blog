package net.briandupreez.pci.chapter6;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/27
 * Time: 2:59 PM
 */
public class ClassifierTest {

    private Classifier classifier;


    @Before
    public void setup(){
        classifier = new Classifier("test.db",true, true);
    }

    @After
    public void after(){
        if(classifier != null){
            classifier.closeConnection();
        }
    }


    @Test
    public void testWeightedProbability() throws Exception {
        double weighted = classifier.weightedProbability("money", "good", 1.0, 0.5);
        Assert.assertEquals(0.25, weighted);
        classifier.sampleTrain();
        weighted = classifier.weightedProbability("money", "good", 1.0, 0.5);
        Assert.assertEquals(0.16666666666666666, weighted);

    }

    @Test
    public void testProbability() throws Exception {
        Assert.assertEquals(0.6666666666666666, classifier.probability("quick", "good"));
    }

    @Test
    public void testFeatureCategoryCount() throws Exception {
        Assert.assertEquals(2, classifier.featureCategoryCount("quick", "good"));
        Assert.assertEquals(1, classifier.featureCategoryCount("casino", "bad"));
    }
}
