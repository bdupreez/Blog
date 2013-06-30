package net.briandupreez.pci.chapter4;


import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Neo4JWebCrawler extends WebCrawler {


    private final GraphDatabaseService graphDb;


    public Neo4JWebCrawler() {
        this.graphDb = CreateDBFactory.createInMemoryDB();
    }


    @Override
    public boolean shouldVisit(final WebURL url) {
        final String href = url.getURL().toLowerCase();
        return !NodeConstants.FILTERS.matcher(href).matches(); // && href.startsWith("http://kiwitobes.com/");
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(final Page page) {

        final String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        final Index<Node> nodeIndex = graphDb.index().forNodes(NodeConstants.PAGE_INDEX);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            //String html = htmlParseData.getHtml();
            List<WebURL> links = htmlParseData.getOutgoingUrls();
            Transaction tx = graphDb.beginTx();
            try {

                final Node pageNode = graphDb.createNode();
                pageNode.setProperty(NodeConstants.URL, url);
                nodeIndex.add(pageNode, NodeConstants.URL, url);

                //get all the words
                final List<String> words = cleanAndSplitString(text);
                int index = 0;
                for (final String word : words) {
                    final Node wordNode = graphDb.createNode();
                    wordNode.setProperty(NodeConstants.WORD, word);
                    wordNode.setProperty(NodeConstants.INDEX, index++);
                    final Relationship relationship = pageNode.createRelationshipTo(wordNode, RelationshipTypes.CONTAINS);
                    relationship.setProperty(NodeConstants.SOURCE, url);
                }

                for (final WebURL webURL : links) {
                    System.out.println("Linking to " + webURL);
                    final Node linkNode = graphDb.createNode();
                    linkNode.setProperty(NodeConstants.URL, webURL.getURL());
                    final Relationship relationship = pageNode.createRelationshipTo(linkNode, RelationshipTypes.LINK_TO);
                    relationship.setProperty(NodeConstants.SOURCE, url);
                    relationship.setProperty(NodeConstants.DESTINATION, webURL.getURL());
                }

                tx.success();
            } finally {
                tx.finish();
            }

        }
    }


    private static List<String> cleanAndSplitString(final String input) {
        if (input != null) {
            final String[] dic = input.toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("\\p{Digit}", "").split("\\s+");
            return Arrays.asList(dic);
        }
        return new ArrayList<>();
    }

}
