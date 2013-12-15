package net.briandupreez.blog.ml.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import junit.framework.Assert;
import net.briandupreez.blog.ml.nlp.neuroph.NeurophDataSetFactory;
import net.briandupreez.blog.ml.nlp.neuroph.NeurophPOSPredictor;
import net.briandupreez.blog.ml.nlp.neuroph.NeurophPOSPredictorNetworkFactory;
import net.briandupreez.blog.ml.nlp.stanford.NlpOptionsFactory;
import net.briandupreez.blog.ml.nlp.stanford.StanfordProcessor;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.junit.Test;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;

/**
 * Test
 * Created by Brian on 2013/12/13.
 */
public class POSPredictorTest {

    private static final String DEVELOPMENT_DATA = "D:/Development/Data/";

    /**
     * Test the Neuroph implementation.
     *
     * @throws Exception error
     */
    @Test
    public void testNeuroph() throws Exception {
        final NeurophPOSPredictorNetworkFactory neurophPosPredictorNetworkFactory = new NeurophPOSPredictorNetworkFactory();

        final NeurophDataSetFactory neurophDataSetFactory = new NeurophDataSetFactory();

        final StanfordProcessor stanfordProcessor = new StanfordProcessor();
        stanfordProcessor.setPipeline(new StanfordCoreNLP(NlpOptionsFactory.tokenizationOnly(false).getNlpProperties()));
        neurophDataSetFactory.setNlpProcessor(stanfordProcessor);

        final DataSet trainingData = neurophDataSetFactory.createTextData(DEVELOPMENT_DATA + "Text/", true, DEVELOPMENT_DATA + "sample.csv", 47, 47);

        final NeuralNetwork neuralNetwork = neurophPosPredictorNetworkFactory.createTrainStore(trainingData);
        final NeurophPOSPredictor neurophPosPredictor = new NeurophPOSPredictor();
        //Load
        neurophPosPredictor.setTrainedNN(neuralNetwork);

        final PartsOfSpeech partsOfSpeech = neurophPosPredictor.predictNextPOS(PartsOfSpeech.NN);
        System.out.println("Neuroph: " + partsOfSpeech);

    }

    /**
     * Test Encog implementation.
     *
     * @throws Exception error
     */
    @Test
    public void testEncog() throws Exception {
        final EncogPOSPredictorNetworkFactory encogPOSPredictorNetworkFactory = new EncogPOSPredictorNetworkFactory();
        final EncogDataSetFactory encogDataSetFactory = new EncogDataSetFactory();

        final StanfordProcessor stanfordProcessor = new StanfordProcessor();
        stanfordProcessor.setPipeline(new StanfordCoreNLP(NlpOptionsFactory.tokenizationOnly(false).getNlpProperties()));
        encogDataSetFactory.setNlpProcessor(stanfordProcessor);

        final MLDataSet trainingData = encogDataSetFactory.createTextData(DEVELOPMENT_DATA + "Text/", true, DEVELOPMENT_DATA + "sample.ser", 47, 47);
        final BasicNetwork network = encogPOSPredictorNetworkFactory.createTrainStore(trainingData, false);

        final EncogPOSPredictor encogPOSPredictor = new EncogPOSPredictor();
        encogPOSPredictor.setTrainedNN(network);

        PartsOfSpeech partsOfSpeech = encogPOSPredictor.predictNextPOS(PartsOfSpeech.NN);
        Assert.assertEquals(PartsOfSpeech.IN, partsOfSpeech);
        System.out.println("Encog: NN ->" + partsOfSpeech);

        partsOfSpeech = encogPOSPredictor.predictNextPOS(PartsOfSpeech.CC);
        Assert.assertEquals(PartsOfSpeech.DT, partsOfSpeech);
        System.out.println("Encog: CC ->" + partsOfSpeech);

        partsOfSpeech = encogPOSPredictor.predictNextPOS(PartsOfSpeech.PRP);
        Assert.assertEquals(PartsOfSpeech.VBP, partsOfSpeech);
        System.out.println("Encog: PRP ->" + partsOfSpeech);

    }

}
