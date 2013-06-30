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
 * WordFrequencyTask.
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

    /**
     * Convert to URL total words.
     * @param result neo4j result
     * @return map
     */
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
                    } else if (NodeConstants.WORD.equals(nextVal)) {
                        wordCount++;
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

}