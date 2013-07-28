package net.briandupreez.pci.chapter6;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Bayes Theorem, is a way of flipping around conditional probabilities.
 * Pr(A | B) = Pr(B | A) x Pr(A)/Pr(B)
 * <p/>
 * User: bdupreez
 * Date: 2013/07/28
 * Time: 12:31 PM
 */
public class NaiveBayes extends Classifier {

    private Map<String, Double> thresholds = new HashMap<>();

    /**
     * Consturctor, with db file location
     *
     * @param dbFile db file
     */
    public NaiveBayes(String dbFile, boolean recreate, boolean train) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //if In Memory db needed:
            //Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            if (recreate) {
                final Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                statement.executeUpdate("drop table if exists fc");
                statement.executeUpdate("drop table if exists cc");
                statement.executeUpdate("create table if not exists fc(feature,category,count)");
                statement.executeUpdate("create table if not exists cc(category,count)");
            }
            if (train) {
                sampleTrain();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Determine the probability of the whole document for category
     *
     * @param text     the document
     * @param category the category
     * @return multiplied probability
     */
    public double documentProbability(final String text, final String category) {
        final Map<String, Integer> features = getWords(text);

        double docProb = 1.0;
        for (final String feature : features.keySet()) {
            docProb *= weightedProbability(feature, category, 1.0, 0.5);
        }
        return docProb;
    }

    /**
     * Calculates the probability of the category
     *
     * @param text     text
     * @param category category
     * @return product of  Pr(Document | Category) and Pr(Category)
     */
    public double prob(final String text, final String category) {
        final double catProb = (double) countForCategory(category) / (double) total();
        final double docProb = documentProbability(text, category);
        return docProb * catProb;
    }

    /**
     * Get the threshold for category
     *
     * @param category the category
     * @return the threshold
     */
    public Double getThreshold(final String category) {
        if (thresholds.containsKey(category)) {
            return thresholds.get(category);
        } else {
            return 1.0;
        }

    }

    /**
     * Add a threshold.
     *
     * @param category  category
     * @param threshold threshold value
     */
    public void addThreshold(final String category, final double threshold) {
        this.thresholds.put(category, threshold);
    }

    public String classify(final String text) {
        final Map<String, Double> probs = new HashMap<>();

        // find the category with the highest probability
        double max = 0.0;
        String best = "";
        for (final String category : categories()) {
            probs.put(category, prob(text, category));
            if (probs.get(category) > max) {
                max = probs.get(category);
                best = category;
            }
        }

        //check the probability > threshold * 2nd
        for (final String cat : probs.keySet()) {
            if (!cat.equals(best)) {
                if (probs.get(cat) * getThreshold(best) > probs.get(best)) {
                    return "unknown";
                }
            }
        }

        return best;
    }
}
