package net.briandupreez.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Search Synonyms
 * Created by Brian on 2014/01/04.
 */
public class SearchSynonymResults extends AbstractSearchResults {

    private final List<String> synonyms = new ArrayList<>();

    public SearchSynonymResults(final String searchTerm) {
        super(searchTerm);
    }

    public List<String> getSynonyms() {
        return Collections.unmodifiableList(synonyms);
    }

    public void addSynonym(final String synonym) {
        synonyms.add(synonym);
    }

    @Override
    public String toString() {
        return "SearchSynonymResults{" +
                "synonyms=" + synonyms +
                '}';
    }
}
