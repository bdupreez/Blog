package net.briandupreez.solr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.HttpSolrServerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.Resource;

/**
 * Solr Context
 * Created by Brian on 2014/01/26.
 */
@Configuration
@EnableSolrRepositories(basePackages = "net.briandupreez.solr.wikipedia")
@ComponentScan(basePackages = "net.briandupreez.solr")
@PropertySource("classpath:solr.properties")
public class SolrContext {

    @Resource
    private Environment environment;

    @Bean
    public HttpSolrServerFactoryBean solrServerFactoryBean() {
        final HttpSolrServerFactoryBean factory = new HttpSolrServerFactoryBean();
        factory.setUrl(environment.getRequiredProperty("solr.server.url.wiki"));
        return factory;
    }

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServerFactoryBean().getObject());
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new JtaTransactionManager();
    }

}
