package net.briandupreez.search;

/**
 * All search settings
 * Created by Brian on 2014/01/05.
 */
public class SearchSettings {


    public static final  String YAHOO_BASE = "http://yboss.yahooapis.com/ysearch";
    public static final String YAHOO_CONSUMER_KEY = "REPLACE ME - consumer key";
    public static final String YAHOO_CONSUMER_SECRET = "REPLACE ME - secret";

    public static final String GOOGLE_API_KEY = "REPLACE ME - google api key";
    public static final String GOOGLE_CX = "REPLACE ME - numbers : alphanumeric";


    //"SearchWeb" , "Search".... using both can give you 10 000 free queries...
    public static final String BING_SEARCH_BASE = "https://api.datamarket.azure.com/Bing/Search/v1/Web";
    public static final String BING_WEB_BASE = "https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web";
    public static final String BING_SYNONYM_BASE = "https://api.datamarket.azure.com/Bing/Synonyms/v1/GetSynonyms";

    public static final String BING_API_KEY = "REPLACE ME - Bing API key";


    public static final String ENCODE_FORMAT = "UTF-8";
    public static final int HTTP_STATUS_OK = 200;

}
