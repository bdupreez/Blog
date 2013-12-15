package net.briandupreez.blog.ml.nlp;


import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

/**
 * Encog implementation
 * Created by Brian on 2013/12/11.
 */
public class EncogDataSetFactory extends NLPDataSetFactory<BasicMLDataSet> {

    @Override
    public BasicMLDataSet createTextData(final String inputLocation, final boolean save, final String saveLocation, final int inputSize, final int outputSize) {
        BasicMLDataSet dataSet = null;
        final Path savePath = FileSystems.getDefault().getPath(saveLocation);
        if (!FileUtils.fileExists(saveLocation)) {
            dataSet = new BasicNeuralDataSet();
            final List<Path> pathList = FileUtils.readDirectory(inputLocation);
            for (final Path path : pathList) {
                final String wholeFile = FileUtils.readWholeFile(path);
                final List<PartsOfSpeech> partsOfSpeechList = nlpProcessor.determinePOSFromText(wholeFile);

                PartsOfSpeech current = null;
                for (final PartsOfSpeech partOfSpeech : partsOfSpeechList) {
                    if (current == null) {
                        current = partOfSpeech;
                        continue;
                    }
                    final int currentId = current.getId();
                    final double[] input = new double[inputSize];
                    input[currentId] = 1.0;
                    final MLData inputData = new BasicMLData(input);

                    final int id = partOfSpeech.getId();
                    final double[] ideal = new double[outputSize];
                    ideal[id] = 1.0;
                    final MLData idealData = new BasicMLData(ideal);

                    current = partOfSpeech;
                    dataSet.add(inputData, idealData);
                }

            }

            if (save) {
                FileUtils.saveBinaryFile(savePath, dataSet);
            }
        } else {
            try {
                dataSet = (BasicMLDataSet) FileUtils.readBinaryFile(savePath);
            } catch (final IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return dataSet;
    }


}
