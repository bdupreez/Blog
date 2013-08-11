package net.briandupreez.pci.chapter7;


import org.javatuples.Pair;

import java.sql.*;
import java.util.*;

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

    /**
     * Divide into true and false
     *
     * @param rows  the data
     * @param col   one which column
     * @param value with value
     * @return tuple, true and false
     */
    @SuppressWarnings("unchecked")
    public Pair<Object[][], Object[][]> divideSet(final Object[][] rows, final int col, final Object value) {

        final ArrayList<Object[]> trueList = new ArrayList<>();
        final ArrayList<Object[]> falseList = new ArrayList<>();
        //So wished we had lambdas in java
        /*
        when trying this with LambdaJ.... lol... ugliest line of java ever... and still didn't work
       falseList.add(filter(not(having(on(List.class).get(col).toString(), equalTo((String) value))), asList(rows)));
        */
        for (final Object[] row : rows) {
            if (value instanceof Integer) {
                if (((Integer) row[col]) >= ((Integer) value)) {
                    trueList.add(row);
                } else {
                    falseList.add(row);
                }
            } else {
                if (row[col].equals(value)) {
                    trueList.add(row);
                } else {
                    falseList.add(row);
                }
            }
        }

        Object[][] trueMatrix = new Object[trueList.size()][];
        trueMatrix = trueList.toArray(trueMatrix);

        Object[][] falseMatrix = new Object[falseList.size()][];
        falseMatrix = falseList.toArray(falseMatrix);

        return new Pair<>(trueMatrix, falseMatrix);
    }


    public Map<String, Double> uniqueCounts(final Object[][] rows) {

        final Map<String, Double> counts = new HashMap<>();

        for (final Object[] row : rows) {
            final String resultVal = (String) row[row.length - 1];
            if (counts.containsKey(resultVal)) {
                counts.put(resultVal, counts.get(resultVal) + 1);
            } else {
                counts.put(resultVal, 1.0);
            }
        }


        return counts;
    }


    /**
     * Probability that a randomly placed item will
     * be in the wrong category
     *
     * @return probability
     */
    public double calculateGiniImpurity(final Object[][] rows) {
        final double total = total();
        final Map<String, Double> countsMap = uniqueCounts(rows);

        double prob = 0.0;
        for (Map.Entry<String, Double> entry : countsMap.entrySet()) {
            double p1 = entry.getValue() / total;
            for (Map.Entry<String, Double> entry2 : countsMap.entrySet()) {
                if (entry.getKey().equals(entry2.getKey())) {
                    continue;
                }
                double p2 = (entry2.getValue()) / total;
                prob += p1 * p2;
            }
        }

        return prob;
    }


    /**
     * Entropy - the amount of disorder in a set
     * Harsher of mixed sets.
     * Entropy is the sum of p(x)log(p(x)) across all
     * the different possible results
     *
     * @return
     */
    public double calculateEntropy(final Object[][] rows) {
        final Map<String, Double> countsMap = uniqueCounts(rows);
        double ent = 0.0;
        for (final Map.Entry<String, Double> entry : countsMap.entrySet()) {
            double p = entry.getValue() / rows.length;
            ent += -p * (Math.log(p) / Math.log(2));
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
            final ResultSet rs = statement.executeQuery("SELECT count(serviceChosen)FROM userData");
            total = (double) rs.getInt("count(serviceChosen)");
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Recursive scoring and building of the tree
     *
     * @param rows the data
     * @return top node
     */
    @SuppressWarnings("unchecked")
    public DecisionNode buildTree(final Object[][] rows) {

        double currentScore = calculateEntropy(rows);

        double bestGain = 0.0;
        Pair<Integer, Object> bestCriteria = null;
        Pair<Object[][], Object[][]> bestSets = null;

        for (int col = 0; col < rows[0].length - 1; col++) {
            //generate a list of different values in the column
            Map<String, Integer> columnValues = new TreeMap<>();
            for (final Object[] row : rows) {
                columnValues.put(row[col].toString(), 1);
            }
            //then try divideSet for each value
            for (final Object columnValue : columnValues.keySet()) {
                final Pair<Object[][], Object[][]> pair = divideSet(rows, col, columnValue);
                //check the gain
                double p = ((double) pair.getValue0().length) / rows.length;
                double gain = currentScore - p * calculateEntropy(pair.getValue0()) - ((1 - p) * calculateEntropy(pair.getValue1()));
                if (gain > bestGain && pair.getValue0().length > 0 && pair.getValue1().length > 0) {
                    bestGain = gain;
                    bestCriteria = new Pair(col, columnValue);
                    bestSets = pair;
                }
            }

        }
        if (bestGain > 0 && bestSets != null) {
            final DecisionNode trueBranch = buildTree(bestSets.getValue0());
            final DecisionNode falseBranch = buildTree(bestSets.getValue1());
            final DecisionNode returnNode = new DecisionNode();
            returnNode.setCol(bestCriteria.getValue0());
            returnNode.setValue(bestCriteria.getValue1());
            returnNode.setTrueBranch(trueBranch);
            returnNode.setFalseBranch(falseBranch);
            return returnNode;
        } else {
            final DecisionNode returnNode = new DecisionNode();
            returnNode.setResults(uniqueCounts(rows));
            return returnNode;
        }
    }


    /**
     * Print to console. recursive
     *
     * @param node   the node
     * @param indent the indent
     */
    public void printTree(final DecisionNode node, final String indent) {
        if (node.getResults() != null) {
            System.out.println(node.getResults());
        } else {
            System.out.println(node.getCol() + ":" + node.getValue().toString() + "? ");
            System.out.print(indent + "T->");
            printTree(node.getTrueBranch(), indent + "\t");
            System.out.print(indent + "F->");
            printTree(node.getFalseBranch(), indent + "\t");
        }
    }


    /**
     * get teh data a matrix
     *
     * @return matrix
     */
    public Object[][] retrieveDataAsMatrix() {

        final Object[][] arrays = new Object[16][5];
        try {
            final Statement statement = connection.createStatement();
            final ResultSet rs = statement.executeQuery("SELECT * FROM userData");
            int i = 0;
            while (rs.next()) {
                arrays[i][0] = rs.getString(1);
                arrays[i][1] = rs.getString(2);
                arrays[i][2] = rs.getString(3);
                arrays[i][3] = rs.getInt(4);
                arrays[i][4] = rs.getString(5);
                i++;
            }
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return arrays;
    }


    /**
     * Classify new data.
     *
     * @param row  the data (minus the result)
     * @param tree the trained tree
     * @return the result based on teh tree
     */
    public Map<String, Double> classify(final Object[] row, final DecisionNode tree) {
        if (tree.getResults() != null) {
            return tree.getResults();
        } else {
            final Object value = row[tree.getCol()];
            DecisionNode branch = null;
            if (value instanceof Integer) {
                if ((Integer) value >= (Integer) tree.getValue()) {
                    branch = tree.getTrueBranch();
                } else {
                    branch = tree.getFalseBranch();
                }
            } else {
                if (value.toString().equals(tree.getValue().toString())) {
                    branch = tree.getTrueBranch();
                } else {
                    branch = tree.getFalseBranch();
                }
            }
            return classify(row, branch);
        }
    }


    /**
     * Classify with missing data
     *
     * @param observation the input
     * @param tree        the tree
     * @return a map with all probabilities from missing data
     */
    public Map<String, Double> missingDataClassify(final Object[] observation, final DecisionNode tree) {

        if (tree.getResults() != null) {
            return tree.getResults();
        } else {
            final Object value = observation[tree.getCol()];
            if (value == null) {
                final Map<String, Double> trueResult = missingDataClassify(observation, tree.getTrueBranch());
                final Map<String, Double> falseResult = missingDataClassify(observation, tree.getFalseBranch());
                double trueCount = 0.0;
                for (final Double tv : trueResult.values()) {
                    trueCount += tv;
                }
                double falseCount = 0.0;
                for (final Double fv : falseResult.values()) {
                    falseCount += fv;
                }
                double trueWeight = trueCount / (trueCount + falseCount);
                double falseWeight = falseCount / (trueCount + falseCount);
                final Map<String, Double> reslt = new HashMap<>();
                for (Map.Entry<String, Double> trueEntry : trueResult.entrySet()) {
                    reslt.put(trueEntry.getKey(), trueEntry.getValue() * trueWeight);
                }

                for (Map.Entry<String, Double> falseEntry : falseResult.entrySet()) {
                    reslt.put(falseEntry.getKey(), falseEntry.getValue() * falseWeight);
                }

                return reslt;

            } else {
                DecisionNode branch = null;
                if (value instanceof Integer) {
                    if ((Integer) value >= (Integer) tree.getValue()) {
                        branch = tree.getTrueBranch();
                    } else {
                        branch = tree.getFalseBranch();
                    }
                } else {
                    if (value.toString().equals(tree.getValue().toString())) {
                        branch = tree.getTrueBranch();
                    } else {
                        branch = tree.getFalseBranch();
                    }
                }
                return missingDataClassify(observation, branch);
            }
        }


    }

    /**
     * Trim down nodes with delta less than min gain.
     * recursive
     *
     * @param tree    the node
     * @param minGain minimum gain
     */
    public void prune(final DecisionNode tree, final double minGain) {
        //if branches aren't leaves remove them.
        if (tree.getTrueBranch().getResults() == null) {
            prune(tree.getTrueBranch(), minGain);
        }
        if (tree.getFalseBranch().getResults() == null) {
            prune(tree.getFalseBranch(), minGain);
        }

        //id both the branches are leaves, can we merge them
        if (tree.getTrueBranch().getResults() != null && tree.getFalseBranch().getResults() != null) {

            final ArrayList<Object[]> trueList = new ArrayList<>();
            for (final Map.Entry<String, Double> entry : tree.getTrueBranch().getResults().entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    trueList.add(new Object[]{entry.getKey()});
                }
            }
            Object[][] trueMatrix = new Object[trueList.size()][];
            trueMatrix = trueList.toArray(trueMatrix);

            final ArrayList<Object[]> falseList = new ArrayList<>();
            for (final Map.Entry<String, Double> entry : tree.getFalseBranch().getResults().entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    falseList.add(new Object[]{entry.getKey()});
                }
            }
            Object[][] falseMatrix = new Object[falseList.size()][];
            falseMatrix = falseList.toArray(falseMatrix);

            final Object[][] combined = new Object[falseList.size() + trueList.size()][];
            System.arraycopy(trueMatrix, 0, combined, 0, trueList.size());
            System.arraycopy(falseMatrix, 0, combined, trueList.size(), falseList.size());

            double delta = calculateEntropy(combined) - (calculateEntropy(trueMatrix) + calculateEntropy(falseMatrix)) / 2;

            if (delta < minGain) {
                tree.setFalseBranch(null);
                tree.setTrueBranch(null);
                tree.setResults(uniqueCounts(combined));
            }

        }
    }


}
