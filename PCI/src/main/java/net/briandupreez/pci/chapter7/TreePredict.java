package net.briandupreez.pci.chapter7;


import org.javatuples.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TreePredict.
 * User: bdupreez
 * Date: 2013/08/09
 * Time: 7:15 AM
 */
public class TreePredict {

    protected Connection connection;


    /**
     * create some data, using in memory db
     */
    public TreePredict() {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("DROP TABLE IF EXISTS userData");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS userData(referrer,location,readFAQ,pagesViewed, serviceChosen)");

            statement.execute("INSERT INTO userData VALUES ('slashdot', 'USA', 'yes', 18, 'None')");
            statement.execute("INSERT INTO userData VALUES ('google', 'France', 'yes', 23, 'Premium')");
            statement.execute("INSERT INTO userData VALUES ('digg', 'USA', 'yes', 24, 'Basic')");
            statement.execute("INSERT INTO userData VALUES ('kiwitobes', 'France', 'yes', 23, 'Basic')");
            statement.execute("INSERT INTO userData VALUES ('google', 'UK', 'no', 21, 'Premium')");
            statement.execute("INSERT INTO userData VALUES ('(direct)', 'New Zealand', 'no', 12, 'None')");
            statement.execute("INSERT INTO userData VALUES ('(direct)', 'UK', 'no', 21, 'Basic')");
            statement.execute("INSERT INTO userData VALUES ('google', 'USA', 'no', 24, 'Premium')");
            statement.execute("INSERT INTO userData VALUES ('slashdot', 'France', 'yes', 19, 'None')");
            statement.execute("INSERT INTO userData VALUES ('digg', 'USA', 'no', 18, 'None')");
            statement.execute("INSERT INTO userData VALUES ('google', 'UK', 'no', 18, 'None')");
            statement.execute("INSERT INTO userData VALUES ('kiwitobes', 'UK', 'no', 19, 'None')");
            statement.execute("INSERT INTO userData VALUES ('digg', 'New Zealand', 'yes', 12, 'Basic')");
            statement.execute("INSERT INTO userData VALUES ('slashdot', 'UK', 'no', 21, 'None')");
            statement.execute("INSERT INTO userData VALUES ('google', 'UK', 'yes', 18, 'Basic')");
            statement.execute("INSERT INTO userData VALUES ('kiwitobes', 'France', 'yes', 19, 'Basic')");

        } catch (final SQLException e) {
            e.printStackTrace();
        }

    }

    public Pair<List, List> divideSet(final String col, final Object value) {

        final ArrayList trueList = listForColumn(col, value, true);
        final ArrayList falseList = listForColumn(col, value, false);
        return new Pair<List, List>(trueList, falseList);
    }

    private ArrayList listForColumn(String col, Object value, boolean exists) {
        final ArrayList trueList = new ArrayList();
        final Statement statement;
        try {
            statement = connection.createStatement();
            String query;
            String equal = "==";
            if (!exists) {
                equal = "!=";
            }
            if (value instanceof Integer) {
                query = String.format("select serviceChosen from userData where %s %s %d", col, equal, (Integer) value);
            } else {
                query = String.format("select serviceChosen from userData where %s %s '%s'", col, equal, value);
            }
            statement.setFetchSize(15);
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                trueList.add(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trueList;
    }


    public Map<String, Integer> uniqueCounts(){
        final Statement statement;
        final Map<String, Integer> counts = new HashMap<>();
        try {
            statement = connection.createStatement();
            final String query = String.format("SELECT serviceChosen, count(serviceChosen) FROM userData GROUP BY serviceChosen");
            statement.setFetchSize(15);
            final ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                counts.put(rs.getString(1), rs.getInt(2));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }


    /**
     * Probability that a randomly placed item will
     * be in the wrong category
     *
     * @return probability
     */
    public double calculateGiniImpurity() {
        final double total = total();
        final Map<String, Integer> countsMap = uniqueCounts();

        double prob = 0.0;
        for (Map.Entry<String, Integer> entry : countsMap.entrySet()) {
            double p1 = ((double) entry.getValue()) / total;
            for (Map.Entry<String, Integer> entry2 : countsMap.entrySet()) {
                if(entry.getKey().equals(entry2.getKey())){
                    continue;
                }
                double p2 = ((double) entry2.getValue()) / total;
                prob +=p1 * p2;
            }
        }

        return prob;
    }


    /**
     * Entropy - the amount of disorder in a set
     * Harsher of mixed sets.
     * Entropy is the sum of p(x)log(p(x)) across all
     * the different possible results
     * @return
     */
    public double calculateEntropy(){
        final Map<String, Integer> countsMap = uniqueCounts();
        final double total = total();
        double ent = 0.0;

        for (final Map.Entry<String, Integer> entry : countsMap.entrySet()) {
           double p = (double) entry.getValue() / total;
            ent = ent - p * (Math.log(p) / Math.log(2));
        }

        return ent;

    }

    /**
     * Add it up.
     *
     * @return total
     */
    public double total() {
        double total = 0;
        try {
            final Statement statement = connection.createStatement();
            statement.setFetchSize(1);
            final ResultSet rs = statement.executeQuery("select count(serviceChosen)from userData");
            total = (double)rs.getInt("count(serviceChosen)");
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public DecisionNode buildTree() {


        return new DecisionNode();

    }
}
