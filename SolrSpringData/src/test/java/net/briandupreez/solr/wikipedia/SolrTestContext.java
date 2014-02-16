package net.briandupreez.solr.wikipedia;

import com.atomikos.icatch.jta.UserTransactionManager;

import net.briandupreez.solr.SolrContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

/**
 * TestConfig
 * Created by Brian on 2014/02/08.
 */
@Configuration
@EnableSolrRepositories(basePackages = "net.briandupreez.solr.wikipedia")
@ComponentScan(basePackages = "net.briandupreez.solr")
@Import({SolrContext.class})
public class SolrTestContext {


    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        final TransactionManager userTransactionManager = new UserTransactionManager();
        return new JtaTransactionManager(userTransactionManager);
    }

}
