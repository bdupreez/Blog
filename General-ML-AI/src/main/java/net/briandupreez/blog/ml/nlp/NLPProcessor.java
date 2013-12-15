package net.briandupreez.blog.ml.nlp;

import java.util.List;

/**
 * NLP processor wrapper
 * Created by Brian on 2013/12/08.
 */
public interface NLPProcessor {

    /**
     * Determine parts of speech
     * @param text  text to be used
     * @return list of parts of speech
     */
    List<PartsOfSpeech> determinePOSFromText(String text);
}
