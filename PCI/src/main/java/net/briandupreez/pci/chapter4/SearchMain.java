package net.briandupreez.pci.chapter4;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import net.briandupreez.pci.chapter4.tasks.*;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/06/18
 * Time: 8:16 AM
 */
public class SearchMain {


    private GraphDatabaseService graphDb = CreateDBFactory.createInMemoryDB();

    public static void main(String[] args) throws Exception {
        final SearchMain main = new SearchMain();
        main.run(false);
    }

    private void run(final boolean cleanAndFetch) throws Exception {

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            if (cleanAndFetch) {

                CreateDBFactory.clearDb();
                setupAndExecuteCrawler();
            }

            final String[] searchTerms = {"java", "spring"};

            List<Callable<TaskResponse>> tasks = new ArrayList<>();
            tasks.add(new WordFrequencyTask(searchTerms));
            tasks.add(new DocumentLocationTask(searchTerms));
            tasks.add(new PageRankTask(searchTerms));
            tasks.add(new NeuralNetworkTask(searchTerms));

            final List<Future<TaskResponse>> results = executorService.invokeAll(tasks);
            final List<Map<String, Double>> resultList = new ArrayList<>();
            for (final Future<TaskResponse> result : results) {
                TaskResponse taskResponse = result.get();
                if (taskResponse.taskClazz.equals(WordFrequencyTask.class)) {
                    addWeighting(taskResponse.resultMap, 1.0);
                    System.out.println("Word Frequency: " + MapUtil.entriesSortedByValues(taskResponse.resultMap, false));
                    resultList.add(taskResponse.resultMap);
                } else if (taskResponse.taskClazz.equals(DocumentLocationTask.class)) {
                    addWeighting(taskResponse.resultMap, 1.0);
                    System.out.println("Document Location: " + MapUtil.entriesSortedByValues(taskResponse.resultMap, false));
                    resultList.add(taskResponse.resultMap);
                } else if (taskResponse.taskClazz.equals(PageRankTask.class)) {
                    addWeighting(taskResponse.resultMap, 2.0);
                    System.out.println("Page Rank: " + MapUtil.entriesSortedByValues(taskResponse.resultMap, false));
                    resultList.add(taskResponse.resultMap);
                } else if (taskResponse.taskClazz.equals(NeuralNetworkTask.class)) {
                    addWeighting(taskResponse.resultMap, 3.0);
                    System.out.println("Neural network Rank: " + MapUtil.entriesSortedByValues(taskResponse.resultMap, false));
                    resultList.add(taskResponse.resultMap);
                }

            }

            final Map<String, Double> finalResults = addAllResults(resultList);
            System.out.println("Final:" + MapUtil.entriesSortedByValues(finalResults, false));

            executorService.shutdown();

        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        }

    }

    private Map<String, Double> addAllResults(final List<Map<String, Double>> resultList) {
        final Map<String, Double> finalResults = new HashMap<>();
        for (final String url : resultList.get(0).keySet()) {
            double sum = 0;
            for (Map<String, Double> map : resultList) {
                sum += map.get(url) == null ? 0 : map.get(url);
            }
            finalResults.put(url, sum);
        }
        return finalResults;
    }

    /**
     * Add weighting factor.
     *
     * @param resultMap map
     * @param weight    weight
     */
    private void addWeighting(final Map<String, Double> resultMap, final double weight) {
        for (final Map.Entry<String, Double> entry : resultMap.entrySet()) {
            entry.setValue(entry.getValue() * weight);

        }
    }

    /**
     * Setup crawler4j
     *
     * @throws Exception error
     */
    private void setupAndExecuteCrawler() throws Exception {
        final String crawlStorageFolder = "resources/crawl/root";
        int numberOfCrawlers = 10;

        final CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setFollowRedirects(false);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(30);

        final PageFetcher pageFetcher = new PageFetcher(config);
        final RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        final RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        final CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("http://www.briandupreez.net");
        controller.start(Neo4JWebCrawler.class, numberOfCrawlers);
    }


    /**
     * Format array of strings for in statement
     *
     * @param args
     * @return
     */
    private String formatArray(final String... args) {
        final String format = new String(new char[args.length]).replace("\0", "'%s',");
        final String result = String.format(format, args);

        return result.substring(0, result.length() - 1);
    }

    private void readData() {

        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("START page=node(*)" +
                " MATCH (page)-[:CONTAINS]->words" +
                " WHERE words.word IN ['python'] " +
                " return page");
//        ExecutionResult result = engine.execute("START page=node:pageIndex(url='http://www.briandupreez.net/')" +
//                " MATCH (page)-[:LINK_TO]->related , (page)-[:CONTAINS]->words" +
//                " WHERE related.url =~ '.*ama.*' AND words.word IN ['python'] " +
//                " return page, related, words");

//        ExecutionResult result = engine.execute("START page=node:pageIndex(url='http://www.amazon.com/Programming-Collective-Intelligence-Building-Applications/dp/0596529325')" +
//                " MATCH (page)-[:LINK_TO]->related , (page)-[:CONTAINS]->words" +
//                " WHERE related.url =~ '.*ama.*' AND words.word IN ['intelligence'] " +
//                " return page, related, words");

        int resultCount = 0;
        for (final Map<String, Object> row : result) {
            resultCount++;

            for (final Map.Entry<String, Object> column : row.entrySet()) {
                System.out.println("Key: " + column.getKey());
                if ("page".equals(column.getKey())) {
                    //pageRelationshipsTraverser((Node) column.getValue());
                    //pageWordsTraverser((Node) column.getValue());
                } else if ("related".equals(column.getKey())) {

                } else if ("word".equals(column.getKey())) {

                }
                final Iterator<String> iter = ((Node) column.getValue()).getPropertyKeys().iterator();
                while (iter.hasNext()) {
                    final String nextVal = iter.next();
                    System.out.println(nextVal + ": " + ((Node) column.getValue()).getProperty(nextVal));
                }


            }

        }
        System.out.printf("Number of results: " + resultCount);

    }


    /**
     * Traverse a page node display all linked urls.
     *
     * @param node page node
     */
    private void pageRelationshipsTraverser(final Node node) {

        final TraversalDescription LINK_TRAVERSAL = Traversal.description()
                .depthFirst()
                .relationships(RelationshipTypes.LINK_TO)
                .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);


        String output = "";
        for (Relationship relationship : LINK_TRAVERSAL.traverse(node).relationships()) {

            output += relationship.getStartNode().getProperty("url") + " -> " +
                    relationship.getType() + " -> " + relationship.getEndNode().getProperty("url") + "\n";
        }

        System.out.println(output);

    }


    /**
     * Traverse a page node display all linked words.
     *
     * @param node page node
     */
    private void pageWordsTraverser(final Node node) {

        final TraversalDescription LINK_TRAVERSAL = Traversal.description()
                .depthFirst()
                .relationships(RelationshipTypes.CONTAINS)
                .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);

        String output = "";
        for (Relationship relationship : LINK_TRAVERSAL.traverse(node).relationships()) {

            output += relationship.getStartNode().getProperty("url") + " -> " +
                    relationship.getType() + " -> " + relationship.getEndNode().getProperty(NodeConstants.WORD) + "\n";
        }

        System.out.println(output);

    }


    private Set<String> determineUniqueURLsContaining(final String... words) {
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute("START page=node(*)" +
                " MATCH (page)-[:CONTAINS]->words" +
                " WHERE words.word IN [" + formatArray(words) + "] " +
                " RETURN DISTINCT page");

        final Set<String> uniqueUrls = new HashSet<>();
        for (final Map<String, Object> row : result) {
            for (final Map.Entry<String, Object> column : row.entrySet()) {
                final Iterator<String> iter = ((Node) column.getValue()).getPropertyKeys().iterator();
                while (iter.hasNext()) {
                    final String nextVal = iter.next();
                    if (NodeConstants.URL.equals(nextVal)) {
                        uniqueUrls.add(((Node) column.getValue()).getProperty(nextVal).toString());
                        System.out.println("Unique URL containing (" + formatArray(words) + "): " + ((Node) column.getValue()).getProperty(nextVal));
                    }
                }
            }
        }
        return uniqueUrls;
    }
}
