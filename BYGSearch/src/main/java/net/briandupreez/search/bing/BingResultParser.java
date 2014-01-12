package net.briandupreez.search.bing;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.briandupreez.search.SearchResult;
import net.briandupreez.search.SearchResultParser;
import net.briandupreez.search.SearchResults;
import net.briandupreez.search.SearchSynonymResults;


import java.io.IOException;

/**
 * Parse the results
 * Created by Brian on 2014/01/04.
 */
public class BingResultParser implements SearchResultParser {

    @Override
    public SearchResults parseWeb(final String searchTerm, final String searchResults){

        final ObjectMapper mapper = new ObjectMapper();
        final SearchResults response = new SearchResults(searchTerm);
        final JsonNode input;
        try {
            input = mapper.readTree(searchResults);
            final JsonNode webResults = input.get("d").get("results");
            for (final JsonNode element: webResults) {
                final SearchResult result = new SearchResult();
                result.setUrl(element.get("Url").asText());
                result.setDisplay(element.get("DisplayUrl").asText());
                result.setDescription(element.get("Description").asText());
                result.setTitle(element.get("Title").asText());
                response.addResult(SearchResults.ResultType.WEB, result);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        }

       return response;
    }

    public SearchSynonymResults parseSynonym(final String searchTerm, final String synonymResults){

        final ObjectMapper mapper = new ObjectMapper();
        final SearchSynonymResults response = new SearchSynonymResults(searchTerm);
        final JsonNode input;
        try {
            input = mapper.readTree(synonymResults);
            final JsonNode webResults = input.get("d").get("results");
            for (final JsonNode element: webResults) {
                response.addSynonym (element.get("Synonym").asText());
            }

        } catch (final IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
