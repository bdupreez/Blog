package net.briandupreez.blog.ml.nlp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POS Predictor
 * Created by Brian on 2013/12/12.
 */
public abstract class POSPredictor {
    /**
     * Predict the next likely POS
     * @param partOfSpeech the current POS
     * @return pos
     */
    public abstract PartsOfSpeech predictNextPOS(PartsOfSpeech partOfSpeech);

    /**
     * Get the Top POS
     * @param results the result
     * @return the pos
     */
    protected PartsOfSpeech nextPOSFromResults(final List<Double> results) {

        final Map<PartsOfSpeech, Double> probabilities = new HashMap<>();
        int index =0;
        for (final Double result : results) {
            probabilities.put(PartsOfSpeech.getPartOfSpeechForId(index), result);
            index++;
        }
        return MapUtil.entriesSortedByValues(probabilities, false).get(0).getKey();
    }

    /**
     * Create a list of all parts with current set to 1
     * @param partOfSpeech the pos
     * @return list
     */
    protected ArrayList<Double> createInputList(final PartsOfSpeech partOfSpeech) {
        final ArrayList<Double> returnList = new ArrayList<>();

        for (final PartsOfSpeech partsOfSpeech : PartsOfSpeech.values()) {
            if(partsOfSpeech.equals(partOfSpeech)){
                returnList.add(1.0);
            } else {
                returnList.add(0.0);
            }
        }
        return returnList;
    }
}
