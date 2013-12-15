package net.briandupreez.blog.ml.nlp.neuroph;

import com.google.common.primitives.Doubles;

import net.briandupreez.blog.ml.nlp.POSPredictor;
import net.briandupreez.blog.ml.nlp.PartsOfSpeech;
import org.neuroph.core.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicts the next probable Part Of Speech.
 * Created by Brian on 2013/12/08.
 */
public class NeurophPOSPredictor extends POSPredictor {

    private NeuralNetwork trainedNN;

    /**
     * Predict the next likely POS
     *
     * @param partOfSpeech the current POS
     * @return pos
     */
    public PartsOfSpeech predictNextPOS(final PartsOfSpeech partOfSpeech) {

        final ArrayList<Double> posDoubles = createInputList(partOfSpeech);
        trainedNN.setInput(Doubles.toArray(posDoubles));
        trainedNN.calculate();
        final double[] output = trainedNN.getOutput();
        final List<Double> results = Doubles.asList(output);

        return nextPOSFromResults(results);

    }

    /**
     * Set the network
     *
     * @param trainedNN the network
     */
    public void setTrainedNN(final NeuralNetwork trainedNN) {
        this.trainedNN = trainedNN;
    }

}
