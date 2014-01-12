package net.briandupreez.search;

import net.briandupreez.search.google.GoogleSearch;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Google test
 * Created by Brian on 2014/01/04.
 */
public class GoogleSearchTest {

    @Test
    public void testGoogleWebSearch() throws Exception {

        BasicConfigurator.configure();
        final GoogleSearch googleSearch = new GoogleSearch();
        final SearchResults search = googleSearch.search("Brian Du Preez");

        System.out.println(search);
        Assert.assertFalse(search.isFailed());

    }
}
