package net.briandupreez.pci.chapter4.tasks;


import com.google.common.collect.Lists;
import net.briandupreez.pci.chapter4.NodeConstants;
import net.briandupreez.pci.chapter4.NormalizationFunctions;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Neural network task, using chapter4 term and fake "clicks" to urls to train.
 * User: bdupreez
 * Date: 2013/06/29
 * Time: 12:41 PM
 */
public class NeuralNetworkTask extends SearchTask implements Callable<TaskResponse> {

    private static final String NETWORK_FILE = "searchterms-network.eg";
    private static final String PATH = "resources/nn/";

    private static int inputNeurons = 0;
    private static int hiddenNeurons = 0;
    private static int ouputNeurons = 0;
    private static final int MAX_EPOCHS = 20;
    private static final double ERROR_RATE = 10.0;
    private static final String[][] trainingStrings = {{"java", "spring"}, {"brian", "ai"},
            {"java", "python"}, {"i", ".net", "use", "maven"}, {"oracle", "weblogic", "java"},
            {"the", "last", "ai", "java", "machine", "learning", "network", "agile", "amazon"}};


    /**
     * Constructor
     *
     * @param terms words to chapter4 for
     */
    public NeuralNetworkTask(final String... terms) {
        super(terms);
    }

    /**
     * Call .. main processing
     *
     * @return the task response
     * @throws Exception error
     */
    @Override
    public TaskResponse call() throws Exception {

        Path path = Paths.get(PATH + NETWORK_FILE);

        final BasicNetwork trainedNetwork;
        if (!Files.exists(path)) {
            final MLDataSet trainingData = createTrainingData();
            trainedNetwork = train(trainingData);
        } else {
            trainedNetwork = (BasicNetwork) EncogDirectoryPersistence.loadObject(path.toFile());
        }

        final MLData inputData = createInputData();
        final MLData outputData = trainedNetwork.compute(inputData);
        System.out.println(outputData);

        //convert output chapter3 to URL, double
        final Map<String, Double> returnMap = new HashMap<>();
        final List<String> allPages = retrieveAllPages();
        for (int i = 0; i < outputData.size(); i++) {
            if (outputData.getData(i) == 1.0) {
                returnMap.put(allPages.get(i), outputData.getData(i));
            }
        }
        final TaskResponse response = new TaskResponse();
        response.taskClazz = this.getClass();

        if (returnMap.size() > 0) {
            response.resultMap = NormalizationFunctions.normalizeMap(returnMap, true);
        }
        return response;


    }


    /**
     * Train the neural net
     *
     * @param dataSet dataset containing
     * @return
     */
    private BasicNetwork train(final MLDataSet dataSet) {

        final BasicNetwork network = EncogUtility.simpleFeedForward(inputNeurons, new Double(inputNeurons * 1.20).intValue(), 0, ouputNeurons, true);
        final MLTrain train = new Backpropagation(network, dataSet);



        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (epoch < MAX_EPOCHS || train.getError() >= ERROR_RATE);

        EncogUtility.evaluate(network, dataSet);

        //persist
        Path path = Paths.get(PATH + NETWORK_FILE);
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                path = Files.createFile(path);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        EncogDirectoryPersistence.saveObject(path.toFile(), network);

        return network;
    }

    /**
     * create training data for neural net.
     *
     * @return training data
     */
    private MLDataSet createTrainingData() {


        final double[][] training = createInputTrainingArray();
        final double[][] ideal = createTrainingIdeal();
        final MLDataSet dataSet = new BasicMLDataSet(training, ideal);
        return dataSet;
    }

    private double[][] createTrainingIdeal() {
        //get all the pages...
        final List<String> allPages = retrieveAllPages();
        ouputNeurons = allPages.size();
        final double[][] ideal = new double[6][allPages.size()];
        int rowInt = 0;
        for (final String[] trainingTerms : trainingStrings) {
            final Set<String> uniquePages = retrieveFakeClickedPages(trainingTerms);
            //pick page based on words... 1 for fake clicked (in this case has terms) -1 for not.
            for (int colInt = 0; colInt < allPages.size(); colInt++) {
                if (uniquePages.contains(allPages.get(colInt))) {
                    ideal[rowInt][colInt] = 1;
                } else {
                    ideal[rowInt][colInt] = -1;
                }

            }
            rowInt++;
        }
        return ideal;
    }

    private double[][] createInputTrainingArray() {
        final StringBuilder bob = new StringBuilder("START word=node(*) WHERE HAS(word.word) ");
        bob.append("RETURN DISTINCT word ORDER BY word.word");

        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final ExecutionResult result = engine.execute(bob.toString());
        final Iterator<Node> wordIterator = result.columnAs("word");
        final Set<String> uniqueWords = new HashSet<>();
        while (wordIterator.hasNext()) {
            uniqueWords.add(wordIterator.next().getProperty(NodeConstants.WORD).toString());
        }

        // pick search terms... mark searched words with 1 others -1
        final double[][] training = new double[6][uniqueWords.size()];
        inputNeurons = uniqueWords.size();
        int rowInt = 0;
        for (final String[] trainingTerms : trainingStrings) {
            final List<String> words = Lists.newArrayList(trainingTerms);
            int colInt = 0;
            for (final String uniqueWord : uniqueWords) {
                if (words.contains(uniqueWord)) {
                    training[rowInt][colInt] = 1;
                } else {
                    training[rowInt][colInt] = -1;
                }
                colInt++;
            }
            rowInt++;
        }

        return training;
    }

    private Set<String> retrieveFakeClickedPages(final String... terms) {

        final StringBuilder bob = new StringBuilder("START page=node(*) MATCH (page)-[:CONTAINS]->words ");
        bob.append("WHERE words.word in [");
        bob.append(formatArray(terms));
        bob.append("] ");
        bob.append("RETURN DISTINCT page ");
        bob.append("ORDER BY page.url");

        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final ExecutionResult result = engine.execute(bob.toString());
        final Iterator<Node> pageIterator = result.columnAs("page");
        final Set<String> uniquePages = new HashSet<>();
        while (pageIterator.hasNext()) {
            final Node node = pageIterator.next();
            uniquePages.add(node.getProperty(NodeConstants.URL).toString());
        }
        return uniquePages;
    }

    private List<String> retrieveAllPages() {

        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final StringBuilder bob = new StringBuilder("START page=node(*) ");
        bob.append("WHERE HAS(page.url) ");
        bob.append("RETURN DISTINCT page ");
        bob.append("ORDER BY page.url");

        final ExecutionResult result = engine.execute(bob.toString());
        Iterator<Node> pageIterator = result.columnAs("page");
        final List<String> allPages = new ArrayList<>();
        while (pageIterator.hasNext()) {
            final Node node = pageIterator.next();
            allPages.add(node.getProperty(NodeConstants.URL).toString());
        }
        return allPages;
    }

    /**
     * Create input data
     *
     * @return input
     */
    private MLData createInputData() {

        // mark terms as 1 others as -1
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final StringBuilder bob = new StringBuilder("START word=node(*) WHERE HAS(word.word) ");
        bob.append("RETURN DISTINCT word ORDER BY word.word");

        final ExecutionResult result = engine.execute(bob.toString());
        final Iterator<Node> wordIterator = result.columnAs("word");
        final Set<String> uniqueWords = new HashSet<>();
        while (wordIterator.hasNext()) {
            uniqueWords.add(wordIterator.next().getProperty(NodeConstants.WORD).toString());
        }

        // pick search terms... mark searched words with 1 others -1
        final double[] input = new double[uniqueWords.size()];
        final List<String> words = Lists.newArrayList(searchTerms);
        int rowInt = 0;
        for (final String uniqueWord : uniqueWords) {
            if (words.contains(uniqueWord)) {
                input[rowInt] = 1;
            } else {
                input[rowInt] = -1;
            }
            rowInt++;
        }


        final MLData dataSet = new BasicMLData(input);
        return dataSet;
    }


}
