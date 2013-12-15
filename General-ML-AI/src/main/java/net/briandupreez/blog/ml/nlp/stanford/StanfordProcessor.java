package net.briandupreez.blog.ml.nlp.stanford;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import net.briandupreez.blog.ml.nlp.NLPProcessor;
import net.briandupreez.blog.ml.nlp.PartsOfSpeech;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * StanfordProcessor.
 * User: Brian
 * Date: 2013/11/27
 * Time: 7:00 PM
 */
public class StanfordProcessor implements NLPProcessor {

    private StanfordCoreNLP pipeline;

    /**
     * Constructor
     */
    public StanfordProcessor() {

    }

    @Override
    public List<PartsOfSpeech> determinePOSFromText(final String text) {

        final List<PartsOfSpeech> returnList = new ArrayList<>();
        final Annotation document = new Annotation(text);
        if (pipeline == null) {
            pipeline = new StanfordCoreNLP(NlpOptionsFactory.tokenizationOnly(false).getNlpProperties());
        }
        pipeline.annotate(document);
        final List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (final CoreMap sentence : sentences) {
            for (final CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                returnList.add(PartsOfSpeech.getPartOfSpeechForId(PartsOfSpeech.getIdForSymbol(pos)));
            }
        }
        return returnList;
    }


    /**
     * Set the pipeline
     *
     * @param pipeline
     */
    public void setPipeline(final StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }
}
