package net.briandupreez.search.bing;


import net.briandupreez.search.SearchSynonymResults;
import net.briandupreez.search.UrlConnectionHandler;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.HttpURLConnection;

import static net.briandupreez.search.SearchSettings.*;


/**
 * Bing search api integration
 * Created by Brian on 2014/01/02.                                                                                l
 */
public class BingSynonym {
    private transient final Log log = LogFactory.getLog(this.getClass());


    public SearchSynonymResults search(final String searchTerm) throws Exception {
        //or ISO-8859-1
        SearchSynonymResults searchSynonymResults = new SearchSynonymResults(searchTerm);

        final String bingUrl = "https://api.datamarket.azure.com/Bing/Synonyms/v1/GetSynonyms?Query=%27" + URIUtil.encode(searchTerm, null, ENCODE_FORMAT) + "%27&$format=JSON";
        final UrlConnectionHandler urlConnectionHandler = new UrlConnectionHandler();
        final HttpURLConnection basicConnection = urlConnectionHandler.createBasicConnection(bingUrl, BING_API_KEY);
        final UrlConnectionHandler.RequestResult result = urlConnectionHandler.processConnection(basicConnection);

        if (result.getResponseCode() == HTTP_STATUS_OK) {
            final BingResultParser bingResultParser = new BingResultParser();
            searchSynonymResults = bingResultParser.parseSynonym(searchTerm, result.getResponse());
        } else {
            searchSynonymResults.setFailed(true);
            log.error("Error in response due to status code = " + result.getResponseCode() + "Response:\n" + result.getResponse());
        }

        return searchSynonymResults;


    }
}
