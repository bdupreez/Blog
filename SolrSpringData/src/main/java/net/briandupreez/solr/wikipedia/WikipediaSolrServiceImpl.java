package net.briandupreez.solr.wikipedia;


import net.briandupreez.solr.documents.WikipediaDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;

/**
 * Solr Service.
 * Created by Brian on 2014/01/26.
 */
@Service
public class WikipediaSolrServiceImpl implements WikipediaSolrService {

    private transient final Log logger = LogFactory.getLog(this.getClass());

    @Resource
    private WikipediaIndexService indexService;

    @Resource
    private WikipediaDocumentRepository repository;


    @Transactional
    @Override
    public WikipediaDocument add(final String id, final String title, final String user, final String userId, final String text, final Date timestamp) {

        final WikipediaDocument wikipediaDocument = new WikipediaDocument();
        wikipediaDocument.setId(id);
        wikipediaDocument.setTitle(title);
        wikipediaDocument.setText(text);
        wikipediaDocument.setUserId(userId);
        wikipediaDocument.setUser(user);
        wikipediaDocument.setTimestamp(timestamp);
        wikipediaDocument.setAll(wikipediaDocument.toString());
        return indexService.add(wikipediaDocument);
    }

    @Transactional
    @Override
    public void deleteById(final String id) {
        indexService.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public WikipediaDocument findById(final String id) {
        final WikipediaDocument wikipediaDocument = repository.findOne(id);
        logger.debug("FOUND: " + wikipediaDocument);
        return wikipediaDocument;
    }


    @Override
    public Collection<WikipediaDocument> findByTitleContains(final String title) {
        return repository.findByTitleContains(title);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<WikipediaDocument> findByTextContains(final String text) {
        return repository.findByTextContains(text);
    }

    @Transactional
    @Override
    public Collection<WikipediaDocument> findByAllContains(final String text) {
        return repository.findByAllContains(text);
    }

}
