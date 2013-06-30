package net.briandupreez.pci.chapter3;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.google.common.primitives.Doubles;
import org.encog.ml.MLCluster;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.kmeans.KMeansClustering;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Predicates.*;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/06/09
 * Time: 9:12 PM
 */
public class FeedReaderTest {
    /**
     * test determineAllUniqueWords
     *
     * @throws Exception
     */
    @Test
    public void testRead() throws Exception {


        final String file = Resources.getResource("short-feedlist.txt").getFile();
        final Set<String> blogWords = determineWordCompleteList(file);
        final Map<String, Map<String, Double>> blogWordCount = countWordsPerBlog(file, blogWords);
        //strip out the outlying words
        stripOutlyingWords(blogWords, blogWordCount);
        performCusteringAndDisplay(blogWordCount);

    }

    private void performCusteringAndDisplay(final Map<String, Map<String, Double>> blogWordCount) {
        final BasicMLDataSet set = new BasicMLDataSet();

        final Map<String, List<Double>> inputMap = new HashMap<>();
        for (final Map.Entry<String, Map<String, Double>> entry : blogWordCount.entrySet()) {
            final Map<String, Double> mainValues = entry.getValue();
            final double[] elements = Doubles.toArray(mainValues.values());

            List<Double> listInput = Doubles.asList(elements);
            inputMap.put(entry.getKey(), listInput);
            set.add(new BasicMLData(elements));
        }

        final KMeansClustering kmeans = new KMeansClustering(3, set);
        kmeans.iteration(150);

        // Display the cluster
        int i = 1;
        for (final MLCluster cluster : kmeans.getClusters()) {
            System.out.println("*** Cluster " + (i++) + " ***");
            final MLDataSet ds = cluster.createDataSet();
            final MLDataPair pair = BasicMLDataPair.createPair(
                    ds.getInputSize(), ds.getIdealSize());
            for (int j = 0; j < ds.getRecordCount(); j++) {
                ds.getRecord(j, pair);
                List<Double> listInput = Doubles.asList(pair.getInputArray());
                System.out.println(Maps.filterValues(inputMap, Predicates.equalTo(listInput)).keySet().toString() + " " + Arrays.toString(pair.getInputArray()));
            }
        }
    }

    private Map<String, Map<String, Double>> countWordsPerBlog(String file, Set<String> blogWords) throws IOException {
        BufferedReader reader;
        String line;

        reader = new BufferedReader(new FileReader(file));
        final Map<String, Map<String, Double>> blogWordCount = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            final Map<String, Double> wordCounts = FeedReader.countWords(line, blogWords);
            blogWordCount.put(line, wordCounts);
        }
        return blogWordCount;
    }

    private Set<String> determineWordCompleteList(final String file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        Set<String> blogWords = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            blogWords = FeedReader.determineAllUniqueWords(line, blogWords);
            System.out.println("Size: " + blogWords.size());
        }
        return blogWords;
    }

    private void stripOutlyingWords(final Set<String> blogWords, final Map<String, Map<String, Double>> blogWordCount) {
        final Iterator<String> wordIter = blogWords.iterator();
        final double listsize = blogWords.size();
        while (wordIter.hasNext()) {
            final String word = wordIter.next();
            double wordCount = 0;
            for (final Map<String, Double> values : blogWordCount.values()) {
                wordCount += values.get(word) != null ? values.get(word) : 0;
            }
            double percentage = (wordCount / listsize) * 100;
            if (percentage < 0.1 || percentage > 20 || word.length() < 3) {
                wordIter.remove();
                for (final Map<String, Double> values : blogWordCount.values()) {
                    values.remove(word);
                }
            } else {
                System.out.println("\t keeping: " + word + " Percentage:" + percentage);
            }
        }
    }

    /**
     * Example
     *
     * @param entry
     * @return
     */
    private Map<String, Integer> filterMinOccurs(final Map.Entry<String, Map<String, Integer>> entry) {
        final Map<String, Integer> wordMap = entry.getValue();
        Predicate<Integer> countAppearances = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer numberOfAppearance) {
                return (numberOfAppearance > 2);
            }
        };
        return Maps.filterValues(wordMap, countAppearances);
    }

    @Test
    public void testGoogleList() {
        List<String> list1 = Lists.newArrayList("1", "2", "3");
        List<String> list2 = Lists.newArrayList("1", "4", "5");
        List<String> list3 = Lists.newArrayList("1", "4", "6");

        boolean result = and(not(in(list1)), in(list2), in(list3)).apply("1");

        System.out.println(result);  // false

        list1 = Lists.newArrayList("A1", "A2", "A3");
        result = compose(in(list1), new Function<String, String>() {
            public String apply(String from) {
                return "A" + from;
            }
        }).apply("1");

        System.out.println(result);  // true
    }


}
