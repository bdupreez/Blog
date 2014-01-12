package net.briandupreez.search.yahoo;


import net.briandupreez.search.BasicWebSearch;
import net.briandupreez.search.SearchResults;
import net.briandupreez.search.UrlConnectionHandler;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.HttpURLConnection;

import static net.briandupreez.search.SearchSettings.*;


/**
 * Yahoo! Search BOSS
 */
public class YahooSearch implements BasicWebSearch {

    private transient final Log log = LogFactory.getLog(this.getClass());

    /**
     * Search
     *
     * @return results
     */
    @Override
    public SearchResults search(final String searchTerm) throws Exception {

        SearchResults searchResults = new SearchResults(searchTerm);
        //replace the + with %20... seems OAuth doesn't like it
        final String url = String.format("%s/web?q=%s", YAHOO_BASE, URIUtil.encode(searchTerm, null, ENCODE_FORMAT)).replace("+", "%20");
        final OAuthConsumer consumer = new DefaultOAuthConsumer(YAHOO_CONSUMER_KEY, YAHOO_CONSUMER_SECRET);

        final String responseBody;
        try {
            final UrlConnectionHandler connectionHandler = new UrlConnectionHandler();
            final HttpURLConnection oAuthConnection = connectionHandler.createOAuthConnection(url, consumer);
            log.info("sending get request to: " + url + " Decoded: " + URIUtil.decode(url));
            final UrlConnectionHandler.RequestResult result = connectionHandler.processConnection(oAuthConnection);

            if (result.getResponseCode() == HTTP_STATUS_OK) {
                responseBody = result.getResponse();
                log.info("Response: " + responseBody);
                if (!responseBody.contains("yahoo:error")) {
                    final YahooResultParser yahooResultParser = new YahooResultParser();
                    searchResults = yahooResultParser.parseWeb(searchTerm, responseBody);
                }
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