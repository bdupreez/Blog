package net.briandupreez.search;

import java.util.*;

/**
 * Generic Search result VO
 * Created by Brian on 2014/01/04.
 */
public class SearchResults extends AbstractSearchResults {

    private final Map<ResultType, List<SearchResult>> results = new HashMap<>();

    public SearchResults(final String searchTerm) {
        super(searchTerm);
    }

    public void addResult(final ResultType resultType, final SearchResult result) {

        if (results.containsKey(resultType)) {
            results.get(resultType).add(result);
        } else {
            final List<SearchResult> resultList = new ArrayList<>();
            resultList.add(result);
            results.put(resultType, resultList);
        }

    }

    public List<SearchResult> getResults(final ResultType resultType) {
        if (results.get(resultType) != null) {
            return Collections.unmodifiableList(results.get(resultType));
        } else {
            return Collections.emptyList();
        }
    }

    public enum ResultType {
        WEB,
        IMAGE,
        VIDEO
    }

    @Override
    public String toString() {
        return "SearchResults{" +
                "results=" + results +
                '}';
    }
}
