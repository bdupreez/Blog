package net.briandupreez.solr.wikipedia;


import net.briandupreez.solr.SolrIndexService;
import net.briandupreez.solr.documents.WikipediaDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Wikipedia index
 * Created by Brian on 2014/01/26.
 */
@Service
public class WikipediaIndexService implements SolrIndexService<WikipediaDocument, String> {

    private transient final Log logger = LogFactory.getLog(this.getClass());

    @Resource
    private WikipediaDocumentRepository repository;

    @Transactional
    @Override
    public WikipediaDocument add(final WikipediaDocument entry) {
        final WikipediaDocument saved = repository.save(entry);
        logger.debug("Saved: " + saved);
        return saved;
    }

    @Transactional
    @Override
    public void delete(final String id) {
        repository.delete(id);
        logger.debug("Deleted ID: " + id);
    }
}
