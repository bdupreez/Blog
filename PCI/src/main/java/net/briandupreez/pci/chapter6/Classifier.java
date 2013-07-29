package net.briandupreez.pci.chapter6;

import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/27
 * Time: 12:44 PM
 */
public class Classifier {

    protected Connection connection;


    /**
     * Default.
     */
    public Classifier() {
    }

    /**
     * Consturctor, with db file location
     *
     * @param dbFile db file
     */
    public Classifier(final String dbFile, final boolean recreate, final boolean train) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
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
     * basic training.
     */
    public void sampleTrain() {
        train("Nobody owns the water.", "good");
        train("the quick rabbit jumps fences", "good");
        train("buy pharmaceuticals now.", "bad");
        train("make quick money at the online casino", "bad");
        train("the quick brown fox jumps", "good");
    }

    /**
     * Creates a map of words and the number of times they appeared.
     *
     * @param input text
     * @return map
     */
    protected Map<String, Integer> getWords(final String input) {
        final List<String> words = new ArrayList<>();
        if (input != null) {
            final String[] dic = input.toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("\\p{Digit}", "").split("\\s+");
            words.addAll(Arrays.asList(dic));
        }
        final Map<String, Integer> wordMap = new HashMap<>();
        for (final String word : words) {
            if (wordMap.containsKey(word)) {
                wordMap.put(word, wordMap.get(word) + 1);
            } else {
                wordMap.put(word, 1);
            }
        }
        return wordMap;
    }


    /**
     * Increase the feature
     *
     * @param feature  feature
     * @param category classification
     */
    public void increaseFeature(final String feature, final String category) {

        try {
            final Statement statement = connection.createStatement();
            final String sql;
            final int count = featureCategoryCount(feature, category);
            if (count == 0) {
                sql = String.format("insert into fc values ('%s', '%s', 1)", feature, category);
            } else {
                sql = String.format("update fc set count=%d where feature='%s' and category='%s'", count + 1, feature, category);
            }
            statement.execute(sql);
        } catch (final SQLException e) {
            e.printStackTrace();
        }


    }


    /**
     * Increate the category
     *
     * @param category category
     */
    public void increaseCategory(final String category) {
        try {
            final Statement statement = connection.createStatement();
            final String sql;
            final int count = countForCategory(category);
            if (count == 0) {
                sql = String.format("insert into cc values ('%s', 1)", category);
            } else {
                sql = String.format("update cc set count=%d where category='%s'", count + 1, category);
            }
            statement.execute(sql);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * the number of times a feature has appeared in a category.
     *
     * @param feature  the feature
     * @param category the category
     * @return the count
     */
    public int featureCategoryCount(final String feature, final String category) {
        try {
            final Statement statement = connection.createStatement();
            final String query = String.format("select count from fc where feature='%s' and" +
                    " category='%s'", feature, category);
            statement.setFetchSize(1);
            final ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("count");
            }
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * The number of items in a category
     *
     * @param category the category
     * @return count
     */
    public int countForCategory(final String category) {
        try {
            final Statement statement = connection.createStatement();
            final String query = String.format("select count from cc where category='%s'", category);
            statement.setFetchSize(1);
            final ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("count");
            }
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Add it up.
     *
     * @return total
     */
    public int total() {
        int total = 0;
        try {
            final Statement statement = connection.createStatement();
            statement.setFetchSize(1);
            final ResultSet rs = statement.executeQuery("select sum(count)from cc");
            total = rs.getInt("sum(count)");
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * The categories.
     *
     * @return set of categories
     */
    public Set<String> categories() {
        final Set<String> cats = new HashSet<>();
        try {
            final Statement statement = connection.createStatement();
            final ResultSet rs = statement.executeQuery("select category from cc");
            while (rs.next()) {
                cats.add(rs.getString("category"));
            }
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return cats;
    }

    /**
     * Train the classifier.
     *
     * @param text     words
     * @param category category (good or bad)
     */
    public void train(final String text, final String category) {

        final Map<String, Integer> words = getWords(text);
        for (final String word : words.keySet()) {
            increaseFeature(word, category);
        }
        increaseCategory(category);

    }

    /**
     * The total number of times this feature appeared in this
     * category divided by the total number of items in this category.
     *
     * @param feature  feature
     * @param category category
     * @return probability
     */
    public double probability(final String feature, final String category) {
        if (countForCategory(category) == 0) {
            return 0.0;
        } else {
            double result = (double) featureCategoryCount(feature, category) / (double) countForCategory(category);
            return result;
        }
    }


    /**
     * Calculate Weighted probability
     *
     * @param feature            the feature
     * @param category           the category
     * @param weight             the weight
     * @param assumedProbability assumed probability
     * @return weighted probability
     */
    public double weightedProbability(final String feature, final String category, final double weight, final double assumedProbability) {
        double basic = probability(feature, category);

        int sum = 0;
        for (final String cat : categories()) {
            sum += featureCategoryCount(feature, cat);
        }

        double weighted = ((weight * assumedProbability) + (sum * basic)) / (weight + sum);
        return weighted;

    }

    /**
     * Close
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}
