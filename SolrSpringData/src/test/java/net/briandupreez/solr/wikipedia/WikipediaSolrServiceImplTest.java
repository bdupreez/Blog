package net.briandupreez.solr.wikipedia;


import net.briandupreez.solr.documents.WikipediaDocument;
import org.apache.log4j.BasicConfigurator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DelegatingSmartContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Test Solr
 * Created by Brian on 2014/02/02.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SolrTestContext.class}, loader = DelegatingSmartContextLoader.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners({TransactionalTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class})
public class WikipediaSolrServiceImplTest {

    @Autowired
    WikipediaSolrService wikipediaSolrService;

    @Before
    public void setup() {
        BasicConfigurator.configure();
    }

    @Test
    public void testAdd() throws Exception {
        Assert.assertNotNull(wikipediaSolrService.add("ID1", "Title", "user", "userId1", "The Text", new DateTime().withDate(2014, 2, 2).toDate()));
    }

    @Test
    public void testDeleteById() throws Exception {

        Assert.assertNotNull(wikipediaSolrService.add("ID1", "Title", "user", "userId1", "The Text", new DateTime().withDate(2014, 2, 2).toDate()));
        wikipediaSolrService.deleteById("ID1");
        Assert.assertNull(wikipediaSolrService.findById("ID1"));

    }

    @Test
    public void testFindById() throws Exception {
         Assert.assertNotNull(wikipediaSolrService.findById("12"));
    }

    @Test
    public void testFindByTitleContains() throws Exception {
        final Collection<WikipediaDocument> anarchy = wikipediaSolrService.findByTitleContains("Anarchy");
        Assert.assertNotNull(anarchy);
        Assert.assertTrue(anarchy.size() > 100);
    }

    @Test
    public void testFindByTextContains() throws Exception {
        final Collection<WikipediaDocument> sparta = wikipediaSolrService.findByTextContains("This is Sparta");
        Assert.assertNotNull(sparta);
        Assert.assertTrue(sparta.size() > 200);
    }


    @Test
    public void testFindByAllContains() throws Exception {
        final Collection<WikipediaDocument> sparta = wikipediaSolrService.findByAllContains("This is Sparta");
        Assert.assertNotNull(sparta);
        Assert.assertTrue(sparta.size() > 300);
    }
}
