package net.briandupreez.blog.ml.nlp;

import com.google.common.primitives.Doubles;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Predicts the next probable Part Of Speech.
 * Created by Brian on 2013/12/08.
 */
public class EncogPOSPredictor extends POSPredictor {

    private BasicNetwork trainedNN;

    /**
     * Predict the next likely POS
     *
     * @param partOfSpeech the current POS
     * @return pos
     */
    @Override
    public PartsOfSpeech predictNextPOS(final PartsOfSpeech partOfSpeech) {

        final ArrayList<Double> posDoubles = createInputList(partOfSpeech);
        final MLData input = new BasicMLData(Doubles.toArray(posDoubles));
        final MLData output = trainedNN.compute(input);
        final List<Double> results = Doubles.asList(output.getData());

        return nextPOSFromResults(results);

    }

    /**
     * Set the network
     *
     * @param trainedNN the network
     */
    public void setTrainedNN(final BasicNetwork trainedNN) {
        this.trainedNN = trainedNN;
    }

}
