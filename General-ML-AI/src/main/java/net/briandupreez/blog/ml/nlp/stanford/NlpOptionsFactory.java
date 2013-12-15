package net.briandupreez.blog.ml.nlp.stanford;

import java.util.Properties;

/**
 * Factory for Stanford NLP options.
 */
public class NlpOptionsFactory {

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that only does term tokenization
     *
     * @param lemmatisation flag to turn lemmatisation on / off during tokenization
     */
    public static NlpOptionsFactory tokenizationOnly(final boolean lemmatisation) {
        return new NlpOptionsFactory(lemmatisation, false, false, false, false, -1, false, false);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does named entity recognition,
     * but does NOT run name disambiguation or coreference analysis
     */
    public static NlpOptionsFactory namedEntityRecognition(final boolean regexNER, final boolean sentenceParser) {
        return new NlpOptionsFactory(true, true, regexNER, sentenceParser, false, -1, false, false);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does named entity recognition
     * coreference analysis.
     *
     * @param corefMaxSentenceDist max sentence distance to evaluate coreference between tokens
     * @param corefPostProcessing  do post procesing of coreference data to trim out singletons
     */
    public static NlpOptionsFactory namedEntitiesWithCoreferenceAnalysis(final boolean regexNER, final int corefMaxSentenceDist, final boolean corefPostProcessing) {
        return new NlpOptionsFactory(true, true, regexNER, true, true, corefMaxSentenceDist, corefPostProcessing, false);
    }

    /**
     * Create NlpOptions configuration class to create a NLP analyzer that does everything.
     */
    public static NlpOptionsFactory all() {
        return new NlpOptionsFactory(true, true, true, true, true, -1, true, true);
    }

    /**
     * @param lemmatisation               enable lemmatisation output for each word
     * @param namedEntityRecognition      enables named entity recognition
     * @param namedEntityRecognitionRegex enables regex based named entity recognition
     * @param sentenceParser              enables sentence parser
     * @param coreferenceAnalysis         enable coreference analysis of input text
     * @param corefMaxSentenceDist        max sentence distance to evaluate coreference between tokens
     * @param corefPostProcessing         do post processing of coreference data to trim out singletons
     * @param sentiment                   does sentiment analysys
     */
    private NlpOptionsFactory(
            final boolean lemmatisation,
            final boolean namedEntityRecognition,
            final boolean namedEntityRecognitionRegex,
            final boolean sentenceParser,
            final boolean coreferenceAnalysis,
            final int corefMaxSentenceDist,
            final boolean corefPostProcessing,
            final boolean sentiment) {

        this.lemmatisation = lemmatisation;
        this.namedEntityRecognition = namedEntityRecognition;
        this.namedEntityRecognitionRegex = namedEntityRecognitionRegex;
        this.sentenceParser = sentenceParser;
        this.coreferenceAnalysis = coreferenceAnalysis;
        this.corefMaxSentenceDist = corefMaxSentenceDist;
        this.corefPostProcessing = corefPostProcessing;
        this.sentiment = sentiment;
    }

    public final boolean lemmatisation;
    public final boolean namedEntityRecognition;
    public final boolean namedEntityRecognitionRegex;
    public final boolean sentenceParser;
    public final boolean coreferenceAnalysis;
    public final int corefMaxSentenceDist;
    public final boolean corefPostProcessing;
    public final boolean sentiment;

    /**
     * the full list of annotators: tokenize, cleanxml, ssplit, pos, lemma, ner, regexner, gender, truecase, parse, dcoref
     */
    public Properties getNlpProperties() {

        final Properties props = new Properties();
        final StringBuilder annotators = new StringBuilder("tokenize, ssplit, pos");

        if (this.lemmatisation) {
            annotators.append(", lemma");
        }

        if (this.namedEntityRecognition) {
            annotators.append(", ner");
        }

        if (this.namedEntityRecognitionRegex) {
            annotators.append(", regexner");
        }

        if (this.sentenceParser) {
            annotators.append(", parse");
        }

        if(this.sentiment) {
            annotators.append(", sentiment");
        }

        if (this.coreferenceAnalysis) {
            if (annotators.indexOf("parse") == -1) {
                annotators.append(", parse");
            }
            annotators.append(", dcoref");
            props.setProperty(edu.stanford.nlp.dcoref.Constants.MAXDIST_PROP, String.valueOf(this.corefMaxSentenceDist));    //-1 no max dist
            props.setProperty(edu.stanford.nlp.dcoref.Constants.POSTPROCESSING_PROP, String.valueOf(this.corefPostProcessing));    //false is default
        }

        props.put("annotators", annotators.toString());

        //options to not prefix backspaces with forward slash
        props.put("tokenize.options", "invertible,ptb3Escaping=true,escapeForwardSlashAsterisk=false,normalizeParentheses=false,normalizeOtherBrackets=false");

        return props;
    }

}
