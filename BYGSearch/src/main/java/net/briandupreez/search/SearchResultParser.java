package net.briandupreez.search;

/**
 * Parser Interface
 * Created by Brian on 2014/01/04.
 */
public interface SearchResultParser {
    SearchResults parseWeb(String searchTerm, String searchResults);
}
