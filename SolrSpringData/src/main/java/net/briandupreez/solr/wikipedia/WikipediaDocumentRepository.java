package net.briandupreez.solr.wikipedia;


import net.briandupreez.solr.documents.WikipediaDocument;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Wikipedia repo.
 * Created by Brian on 2014/01/26.
 */
@Repository
public interface WikipediaDocumentRepository extends SolrCrudRepository<WikipediaDocument, String> {

    @Query("title:*?0*")
    Collection<WikipediaDocument> findByTitleContains(final String title);

    @Query("text:?0*")
    Collection<WikipediaDocument> findByTextContains(final String text);

    @Query("title:*?0* OR text:?0*")
    Collection<WikipediaDocument> findByAllContains(final String text);

}
