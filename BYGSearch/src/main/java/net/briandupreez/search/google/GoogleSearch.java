package net.briandupreez.search.google;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import net.briandupreez.search.BasicWebSearch;
import net.briandupreez.search.SearchResults;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;

import static net.briandupreez.search.SearchSettings.*;


/**
 * Google
 * Created by Brian on 2014/01/04.
 */
public class GoogleSearch implements BasicWebSearch {

    private transient final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public SearchResults search(final String query) throws Exception {

        final Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), new DisableTimeoutRequest());
        final SearchResults searchResults = new SearchResults(query);
        try {
            final Customsearch.Cse.List list = customsearch.cse().list(query);
            list.setKey(GOOGLE_API_KEY);
            list.setCx(GOOGLE_CX);

            final Search results = list.execute();
            final List<Result> items = results.getItems();
            for (final Result result : items) {
                final GoogleSearchResult searchResult = new GoogleSearchResult();
                searchResult.setTitle(result.getTitle());
                searchResult.setDisplay(result.getDisplayLink());
                searchResult.setUrl(result.getFormattedUrl());
                searchResult.setDescription(result.getSnippet());
                searchResult.setPagemap(result.getPagemap());
                searchResult.setMime(result.getMime());
                searchResult.setLink(result.getLink());
                searchResult.setKind(result.getKind());
                searchResult.setHtmlTitle(result.getHtmlTitle());
                searchResult.setHtmlSnippet(result.getHtmlSnippet());
                searchResult.setFormattedUrl(result.getFormattedUrl());
                searchResult.setFileFormat(result.getFileFormat());
                searchResults.addResult(SearchResults.ResultType.WEB, searchResult);
            }

        } catch (final IOException e) {
            searchResults.setFailed(true);
            logger.error("Google Search Error", e);
        }

        return searchResults;

    }

    public class DisableTimeoutRequest implements HttpRequestInitializer {
        public void initialize(final HttpRequest request) {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
        }
    }
}
