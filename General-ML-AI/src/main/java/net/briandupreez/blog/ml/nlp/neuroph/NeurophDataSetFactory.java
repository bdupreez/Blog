package net.briandupreez.blog.ml.nlp.neuroph;

import net.briandupreez.blog.ml.nlp.FileUtils;
import net.briandupreez.blog.ml.nlp.NLPDataSetFactory;
import net.briandupreez.blog.ml.nlp.PartsOfSpeech;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import java.nio.file.Path;
import java.util.List;

/**
 * Create some data .
 * Created by Brian on 2013/12/08.
 */
public class NeurophDataSetFactory extends NLPDataSetFactory<DataSet> {

    /**
     * Create Text data.
     *
     * @param save         save the data to file
     * @param saveLocation the absolute location including file for output to be saved
     * @return the data
     */
    @Override
    public DataSet createTextData(final String inputLocation, final boolean save, final String saveLocation, final int inputSize, final int outputSize) {

        final DataSet dataSet;
        if (!FileUtils.fileExists(saveLocation)) {
            dataSet = new DataSet(inputSize, outputSize);
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

                    final int id = partOfSpeech.getId();
                    final double[] doubles = new double[outputSize];
                    doubles[id] = 1.0;
                    final DataSetRow dataSetRow = new DataSetRow(input, doubles);
                    dataSet.addRow(dataSetRow);
                    current = partOfSpeech;
                }

            }

            if (save) {
                dataSet.saveAsTxt(saveLocation, ",");
            }
        } else {
             dataSet = DataSet.createFromFile(saveLocation,inputSize,outputSize,",");
        }
        return dataSet;
    }


}
