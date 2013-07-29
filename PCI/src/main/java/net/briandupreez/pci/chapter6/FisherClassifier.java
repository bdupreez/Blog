package net.briandupreez.pci.chapter6;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/28
 * Time: 9:39 PM
 */
public class FisherClassifier extends Classifier {


    /**
     * Constructor.
     * Using in memory example
     */
    public FisherClassifier(final boolean train) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("create table if not exists fc(feature,category,count)");
            statement.executeUpdate("create table if not exists cc(category,count)");

            if (train) {
                sampleTrain();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Probably that item with feature will be in a category
     *
     * @param feature  the feature
     * @param category the category
     * @return probability
     */
    @Override
    public double probability(final String feature, final String category) {


        double clf = super.probability(feature, category);
        if (clf == 0) {
            return 0;
        }

        //the frequency of this feature in all the categories
        double freqSum = 0.0;
        for (final String cat : categories()) {
            freqSum += super.probability(feature, cat);
        }

        //The probability if the frequency in ths category divided by the overall frequency
        return clf / freqSum;

    }

    /**
     * Multiply all probabilities together then take log, and * result by -2
     *
     * @param text     the text
     * @param category the category
     * @return probability
     */
    public double fisherProb(final String text, final String category) {

        final Map<String, Integer> features = getWords(text);

        double docProb = 1.0;
        for (final String feature : features.keySet()) {
            docProb *= weightedProbability(feature, category, 1.0, 0.5);
        }

        //This is the chance that it's not in the cat
        docProb = -2 * Math.log(docProb);

        return inverseChi2(docProb, features.size() * 2);

    }


    public double inverseChi2(final double chi, final double df){
        double m = chi / 2.0;
        double sum = Math.exp(-m);
        double term = Math.exp(-m);
        for(double i = 1.0; i < df/2;i++ ){
            term *= m/i;
            sum +=term;
        }

        return Math.min(sum, 1.0);
    }


}
