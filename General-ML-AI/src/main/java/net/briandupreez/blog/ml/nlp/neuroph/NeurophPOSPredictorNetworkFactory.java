package net.briandupreez.blog.ml.nlp.neuroph;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.TransferFunctionType;

/**
 * Create the POS predictor network
 * Created by Brian on 2013/12/08.
 */
public class NeurophPOSPredictorNetworkFactory implements LearningEventListener {

    public static final String POS_PREDICTION_PERCEPTRON_NNET = "POSPredictionPerceptron.nnet";

    /**
     * Train,  Store and test
     */
    public NeuralNetwork createTrainStore(final DataSet trainingSet) {


        // create multi layer perceptron
        final MultiLayerPerceptron predictPOSPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, trainingSet.getInputSize(), 36, 24, trainingSet.getOutputSize());
        final MomentumBackpropagation momentumBackpropagation = new MomentumBackpropagation();

        momentumBackpropagation.setMomentum(0.7);
        momentumBackpropagation.setLearningRate(0.2);
        momentumBackpropagation.setMaxIterations(10);
        momentumBackpropagation.setBatchMode(true);
        momentumBackpropagation.addListener(this);

        // Resilient just seems to hag during training.
        // final ResilientPropagation resilientPropagation = new ResilientPropagation();

        predictPOSPerceptron.setLearningRule(momentumBackpropagation);

        // learn the training set
        System.out.println("Training neural network...");
        predictPOSPerceptron.learn(trainingSet);

        // save trained neural network
        predictPOSPerceptron.save(POS_PREDICTION_PERCEPTRON_NNET);

        // load saved neural network
        return NeuralNetwork.createFromFile(POS_PREDICTION_PERCEPTRON_NNET);
    }

    /**
     * handle learning event
     *
     * @param event event
     */
    @Override
    public void handleLearningEvent(final LearningEvent event) {
        final BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
    }


}
