package net.briandupreez.solr.wikipedia;



import net.briandupreez.solr.documents.WikipediaDocument;

import java.util.Collection;
import java.util.Date;

/**
 * Wkipedia Interface
 * Created by Brian on 2014/01/26.
 */
public interface WikipediaSolrService {

    WikipediaDocument add(final String id, final String title, final String user, final String userId, final String text, final Date timestamp);
    void deleteById(final String id);
    WikipediaDocument findById(final String id);
    Collection<WikipediaDocument> findByTitleContains(final String title);
    Collection<WikipediaDocument> findByTextContains(final String text);
    Collection<WikipediaDocument> findByAllContains(final String text);

}
