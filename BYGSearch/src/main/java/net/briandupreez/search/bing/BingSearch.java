package net.briandupreez.search.bing;


import net.briandupreez.search.BasicWebSearch;
import net.briandupreez.search.SearchResults;
import net.briandupreez.search.UrlConnectionHandler;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.HttpURLConnection;

import static net.briandupreez.search.SearchSettings.*;

/**
 * Bing search api integration
 * Created by Brian on 2014/01/02.
 */
public class BingSearch implements BasicWebSearch {

    private static transient final Log log = LogFactory.getLog(BingSearch.class);

    @Override
    public SearchResults search(final String searchTerm) throws Exception {
        final String bingUrl = String.format("%s?Query=%%27%s%%27&$format=JSON", BING_WEB_BASE, URIUtil.encode(searchTerm, null, ENCODE_FORMAT));
        SearchResults searchResults = new SearchResults(searchTerm);
        try {
            final UrlConnectionHandler urlConnectionHandler = new UrlConnectionHandler();
            final HttpURLConnection basicConnection = urlConnectionHandler.createBasicConnection(bingUrl, BING_API_KEY);
            final UrlConnectionHandler.RequestResult result = urlConnectionHandler.processConnection(basicConnection);
            if (result.getResponseCode() == HTTP_STATUS_OK) {
                final BingResultParser bingResultParser = new BingResultParser();
                searchResults = bingResultParser.parseWeb(searchTerm, result.getResponse());
            } else {
                searchResults.setFailed(true);
                log.error("Error in response due to status code = " + result.getResponseCode() + "Response:\n" + result.getResponse());
            }

        } catch (final Exception e) {
            searchResults.setFailed(true);
            log.error("Search Error", e);

        }
        return searchResults;
    }

}
