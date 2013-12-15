package net.briandupreez.blog.ml.nlp;

/**
 * NLP Data factory
 * Created by Brian on 2013/12/11.
 */
public abstract class NLPDataSetFactory<T> {

    protected NLPProcessor nlpProcessor;

    /**
     * Create Text data.
     *
     * @param inputLocation the inputLocation
     * @param save          save the data to file
     * @param saveLocation  the absolute location including file for output to be saved
     * @param inputSize     input
     * @param outputSize    output
     * @return the data
     */
    protected abstract T createTextData(String inputLocation, boolean save, String saveLocation, final int inputSize, final int outputSize);


    /**
     * Setter
     * @param nlpProcessor the processor
     */
    public void setNlpProcessor(final NLPProcessor nlpProcessor) {
        this.nlpProcessor = nlpProcessor;
    }
}
