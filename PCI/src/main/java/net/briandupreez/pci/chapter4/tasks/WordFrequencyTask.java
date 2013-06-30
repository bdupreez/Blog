package net.briandupreez.pci.chapter4.tasks;

import net.briandupreez.pci.chapter4.NodeConstants;
import net.briandupreez.pci.chapter4.NormalizationFunctions;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/06/24
 * Time: 7:18 PM
 */

public final class WordFrequencyTask extends SearchTask implements Callable<TaskResponse> {

    /**
     * Constructor
     *
     * @param terms words to chapter4 for
     */
    public WordFrequencyTask(final String... terms) {
        super(terms);
    }

    /**
     * The Callable Method to do the processing
     *
     * @return results
     */
    public TaskResponse call() {
        final ExecutionResult result = executeQuery(searchTerms);
        final Map<String, Double> returnMap = convertToUrlTotalWords(result);

        final TaskResponse response = new TaskResponse();
        response.taskClazz = this.getClass();
        response.resultMap = NormalizationFunctions.normalizeMap(returnMap, false);
        return response;
    }

    private Map<String, Double> convertToUrlTotalWords(final ExecutionResult result) {
        final Map<String, Double> uniqueUrls = new HashMap<>();
        for (final Map<String, Object> row : result) {
            double wordCount = 0.0;
            String currentURL = null;

            for (final Map.Entry<String, Object> entry : row.entrySet()) {
                final Iterator<String> iter = ((Node) entry.getValue()).getPropertyKeys().iterator();

                while (iter.hasNext()) {
                    final String nextVal = iter.next();
                    if (NodeConstants.URL.equals(nextVal)) {
                        currentURL = ((Node) entry.getValue()).getProperty(nextVal).toString();
                        //System.out.println("URL: " + currentURL);
                    } else if (NodeConstants.WORD.equals(nextVal)) {
                        //final String word = ((Node) entry.getValue()).getProperty(NodeConstants.WORD).toString();
                        wordCount++;
                        //System.out.println("From (" + word + ") Word: " + word + " Count: " + wordCount +
                        //       " Index: " + ((Node) entry.getValue()).getProperty(NodeConstants.INDEX));
                    }
                }

            }
            if (uniqueUrls.containsKey(currentURL)) {
                uniqueUrls.put(currentURL, uniqueUrls.get(currentURL) + wordCount);
            } else {
                uniqueUrls.put(currentURL, wordCount);
            }
        }
        return uniqueUrls;
    }


    /**
     * Takes result and makes a map counts
     *
     * @param result the query result
     * @return map

    private Map<String, Map<String, Double>> convertToURLWordCountMap(final ExecutionResult result) {
    final Map<String, Map<String, Double>> uniqueUrls = new HashMap<>();
    for (final Map<String, Object> row : result) {
    String currentURL = null;
    Map<String, Double> wordMap = new HashMap<>();

    for (final Map.Entry<String, Object> entry : row.entrySet()) {
    final Iterator<String> iter = ((Node) entry.getValue()).getPropertyKeys().iterator();

    while (iter.hasNext()) {
    final String nextVal = iter.next();
    if (NodeConstants.URL.equals(nextVal)) {
    currentURL = ((Node) entry.getValue()).getProperty(nextVal).toString();
    //System.out.println("URL: " + currentURL);
    } else if (NodeConstants.WORD.equals(nextVal)) {
    // add
    final String word = ((Node) entry.getValue()).getProperty(NodeConstants.WORD).toString();
    if (wordMap.containsKey(word)) {
    wordMap.put(word, (wordMap.get(word) + 1.0));
    } else {
    wordMap.put(word, 1.0);
    }
    //System.out.println("From (" + word + ") Word: " + word + " Count: " + wordMap.get(word) +
    //        " Index: " + ((Node) entry.getValue()).getProperty(NodeConstants.INDEX));
    }
    }

    }
    if (uniqueUrls.containsKey(currentURL)) {
    final Map<String, Double> tempMap = uniqueUrls.get(currentURL);
    for (final Map.Entry<String, Double> entry : wordMap.entrySet()) {
    if (tempMap.containsKey(entry.getKey())) {
    tempMap.put(entry.getKey(), tempMap.get(entry.getKey()) + entry.getValue());
    } else {
    tempMap.put(entry.getKey(), entry.getValue());
    }
    }
    wordMap = tempMap;
    }
    System.out.println("Url: " + currentURL + " words: " + wordMap);
    uniqueUrls.put(currentURL, wordMap);
    }
    return uniqueUrls;
    }
     */
}