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
import java.util.concurrent.*;

/**
 * SearchMain.
 * User: bdupreez
 * Date: 2013/06/18
 * Time: 8:16 AM
 */
public class SearchMain {


    /**
     * Main
     * @param args args
     * @throws Exception error
     */
    public static void main(String[] args) throws Exception {
        final SearchMain main = new SearchMain();
        main.run(false);
    }

    /**
     * Run
     * @param cleanAndFetch clean re-crawl and store the information
     * @throws Exception error
     */
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
            final List<Map<String, Double>> resultList = handleResults(results);

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

    /**
     * Handle all the future results.
     * @param results list of task responses
     * @return results
     * @throws InterruptedException error
     * @throws ExecutionException error
     */
    private List<Map<String, Double>> handleResults(final List<Future<TaskResponse>> results) throws InterruptedException, ExecutionException {
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
        return resultList;
    }

    /**
     * Sum up the results.
     * @param resultList results to sum
     * @return summed results
     */
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


}
