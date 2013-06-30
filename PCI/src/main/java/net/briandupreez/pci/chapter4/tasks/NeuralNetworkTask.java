package net.briandupreez.pci.chapter4.tasks;


import net.briandupreez.pci.chapter4.NormalizationFunctions;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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

    private static int inputNeurons = 4;
    private static int hiddenNeurons = 6;
    private static int ouputNeurons = 4;
    private static final int MAX_EPOCHS = 1000;
    private static final double ERROR_RATE = 0.01;


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
            trainedNetwork = (BasicNetwork)EncogDirectoryPersistence.loadObject(path.toFile());
        }

        final MLData inputData = createInputData();
        final MLData outputData = trainedNetwork.compute(inputData);
        System.out.println(outputData);

        //convert output chapter3 to URL, double
        final Map<String, Double> returnMap = new HashMap<>();
        returnMap.put("http://www.briandupreez.net/", outputData.getData(0));

        final TaskResponse response = new TaskResponse();
        response.taskClazz = this.getClass();
        response.resultMap = NormalizationFunctions.normalizeMap(returnMap, true);
        return response;


    }


    /**
     * Train the neural net
     * @param dataSet dataset containing
     * @return
     */
    private BasicNetwork train(final MLDataSet dataSet) {

        final BasicNetwork network = EncogUtility.simpleFeedForward(inputNeurons, hiddenNeurons, 0, ouputNeurons, true);
        final MLTrain train = new Backpropagation(network, dataSet);

        EncogUtility.trainToError(train, ERROR_RATE);
        EncogUtility.evaluate(network, dataSet);

        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (epoch < MAX_EPOCHS);

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
     * create training chapter3 for neural net.
     *
     * @return training chapter3
     */
    private MLDataSet createTrainingData() {

        //Get all the words , ordered...
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final StringBuilder bob = new StringBuilder("START word=node(*)");
        bob.append("RETURN DISTINCT word");

        engine.execute(bob.toString());


        // pick chapter4 terms... mark searched words with 1 others -1
        final double[][] training = new double[2][4];
        training[0][0] = -1.0;
        training[0][1] = 1.0;
        training[0][2] = 1.0;
        training[0][3] = -1.0;

        training[1][0] = -1.0;
        training[1][1] = 1.0;
        training[1][2] = -1.0;
        training[1][3] = -1.0;

        //get all the pages, ordered..
/*        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final StringBuilder bob = new StringBuilder("START page=node(*) MATCH (page)-[:CONTAINS]->words ");
        bob.append("WHERE words.word in [");
        bob.append(formatArray(searchTerms));
        bob.append("] ");
        bob.append("RETURN DISTINCT page");

        engine.execute(bob.toString());*/


        //pick page based on words... 1 for clicked -1 for not.
        final double[][] ideal = new double[2][4];
        ideal[0][0] = 1.0;
        ideal[0][1] = -1.0;
        ideal[0][2] = -1.0;
        ideal[0][3] = -1.0;

        ideal[1][0] = -1.0;
        ideal[1][1] = 1.0;
        ideal[1][2] = -1.0;
        ideal[1][3] = -1.0;

        final MLDataSet dataSet = new BasicMLDataSet(training, ideal);
        return dataSet;
    }

    /**
     * Create input data
     * @return input
     */
    private MLData createInputData() {

        //this.searchTerms;
        // get all the words...

        // mark terms as 1 others as -1
        final double[] input = {-1, 1, 1, -1};

        final MLData dataSet = new BasicMLData(input);
        return dataSet;
    }


}
