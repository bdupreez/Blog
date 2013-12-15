package net.briandupreez.blog.ml.nlp;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.RPROPType;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Create the POS predictor network
 * Created by Brian on 2013/12/08.
 */
public class EncogPOSPredictorNetworkFactory {

    public static final String POS_PREDICTION_PERCEPTRON_NNET = "EncogPOSPredictionPerceptron.eg";
    private static final int MAX_EPOCHS = 200;
    private static final double ERROR_RATE = 0.01;

    /**
     * Train,  Store and test
     *
     * @param trainingSet the data
     * @param overwrite   retrain and overwrite
     */
    public BasicNetwork createTrainStore(final MLDataSet trainingSet, final boolean overwrite) {

        Path path = Paths.get(POS_PREDICTION_PERCEPTRON_NNET);

        final BasicNetwork network;

        if (overwrite) {
            network = EncogUtility.simpleFeedForward(trainingSet.getInputSize(), 36, 36, trainingSet.getIdealSize(), true);

            final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
            train.setRPROPType(RPROPType.iRPROPp);

            int epoch = 1;

            do {
                train.iteration();
                System.out.println("Epoch #" + epoch + " Error:" + train.getError());
                epoch++;
            } while (train.getError() >= ERROR_RATE && epoch < MAX_EPOCHS);

            try {

                Files.deleteIfExists(path);
                path = Files.createFile(path);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            EncogDirectoryPersistence.saveObject(path.toFile(), network);

        } else {
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(path.toFile());
        }
        return network;
    }


}
