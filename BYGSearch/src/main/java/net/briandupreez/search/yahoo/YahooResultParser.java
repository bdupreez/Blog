package net.briandupreez.search.yahoo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.briandupreez.search.SearchResult;
import net.briandupreez.search.SearchResultParser;
import net.briandupreez.search.SearchResults;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Parse the results
 * Created by Brian on 2014/01/04.
 */
public class YahooResultParser implements SearchResultParser {

    private static final Logger log = Logger.getLogger(YahooResultParser.class);


    @Override
    public SearchResults parseWeb(final String searchTerm, final String searchResults){

        final ObjectMapper mapper = new ObjectMapper();
        final SearchResults response = new SearchResults(searchTerm);
        final JsonNode input;
        try {
            input = mapper.readTree(searchResults);
            final JsonNode webResults = input.get("bossresponse").get("web").get("results");
            for (final JsonNode element: webResults) {
                final SearchResult result = new SearchResult();
                result.setDescription(element.get("abstract").asText());
                result.setTitle(element.get("title").asText());
                result.setDisplay(element.get("dispurl").asText());
                result.setUrl(element.get("url").asText());

                response.addResult(SearchResults.ResultType.WEB, result);

            }

        } catch (final IOException e) {
            log.error("Parser Error", e);
            throw new RuntimeException("Result Parser Failure", e);
        }

        return response;
    }

}
