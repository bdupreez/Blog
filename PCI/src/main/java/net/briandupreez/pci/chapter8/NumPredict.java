package net.briandupreez.pci.chapter8;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * NumPredict.
 * User: bdupreez
 * Date: 2013/08/12
 * Time: 8:29 AM
 */
public class NumPredict {


    /**
     * Determine the wine price.
     *
     * @param rating rating
     * @param age    age
     * @return the price
     */
    public double winePrice(final double rating, final double age) {
        final double peakAge = rating - 50;

        //Calculate the price based on rating
        double price = rating / 2;

        if (age > peakAge) {
            //goes bad in 10 years
            price *= 5 - (age - peakAge) / 2;
        } else {
            //increases as it reaches its peak
            price *= 5 * ((age + 1)) / peakAge;
        }

        if (price < 0) {
            price = 0.0;
        }

        return price;
    }


    /**
     * Data Generator
     *
     * @return data
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, List<Double>>> createWineSet1() {

        final List<Map<String, List<Double>>> wineList = new ArrayList<>();
        for (int i = 0; i < 300; i++) {

            double rating = Math.random() * 50 + 50;
            double age = Math.random() * 50;

            double price = winePrice(rating, age);
            price *= (Math.random() * 0.2 + 0.9);

            final Map<String, List<Double>> map = new HashMap<>();
            final List<Double> input = new LinkedList<>();
            input.add(rating);
            input.add(age);
            map.put("input", input);
            final List<Double> result = new ArrayList();
            result.add(price);
            map.put("result", result);
            wineList.add(map);

        }


        return wineList;
    }


    /**
     * Data Generator
     *
     * @return data
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, List<Double>>> createWineSet2() {

        final List<Map<String, List<Double>>> wineList = new ArrayList<>();
        for (int i = 0; i < 300; i++) {

            double rating = Math.random() * 50 + 50;
            double age = Math.random() * 50;
            final Random random = new Random();
            double aisle = (double) random.nextInt(20);
            double[] sizes = new double[]{375.0, 750.0, 1500.0};
            double bottleSize = sizes[random.nextInt(3)];


            double price = winePrice(rating, age);
            price *= (bottleSize / 750);
            price *= (Math.random() * 0.2 + 0.9);

            final Map<String, List<Double>> map = new HashMap<>();
            final List<Double> input = new LinkedList<>();
            input.add(rating);
            input.add(age);
            input.add(aisle);
            input.add(bottleSize);
            map.put("input", input);
            final List<Double> result = new ArrayList();
            result.add(price);
            map.put("result", result);
            wineList.add(map);

        }


        return wineList;
    }


    /**
     * Rescale
     *
     * @param data  data
     * @param scale the scales
     * @return scaled data
     */
    public List<Map<String, List<Double>>> rescale(final List<Map<String, List<Double>>> data, final List<Double> scale) {
        final List<Map<String, List<Double>>> scaledData = new ArrayList<>();
        for (final Map<String, List<Double>> dataItem : data) {
            final List<Double> scaledList = new LinkedList<>();
            for (int i = 0; i < scale.size(); i++) {
                scaledList.add(scale.get(i) * dataItem.get("input").get(i));
            }
            dataItem.put("input", scaledList);

            scaledData.add(dataItem);
        }
        return scaledData;
    }


    /**
     * Determine all the distances from a list
     *
     * @param data all the data
     * @param vec1 one list
     * @return all the distances
     */
    public List<Pair<Double, Integer>> determineDistances(final List<Map<String, List<Double>>> data, final List<Double> vec1) {
        final List<Pair<Double, Integer>> distances = new ArrayList<>();
        int i = 1;
        for (final Map<String, List<Double>> map : data) {
            final List<Double> vec2 = map.get("input");

            distances.add(new Pair(EuclideanDistanceScore.distanceList(vec1, vec2), i++));
        }

        Collections.sort(distances);

        return distances;
    }


    /**
     * Use kNN to estimate a new price
     *
     * @param data all the data
     * @param vec1 new fields to price
     * @param k    the amount of neighbours
     * @return the estimated price
     */
    public double knnEstimate(final List<Map<String, List<Double>>> data, final List<Double> vec1, final int k) {
        final List<Pair<Double, Integer>> distances = determineDistances(data, vec1);
        double avg = 0.0;

        for (int i = 0; i <= k; i++) {
            int idx = distances.get(i).getValue1();
            avg += data.get(idx - 1).get("result").get(0);
        }
        avg = avg / k;
        return avg;
    }


    /**
     * KNN using a weighted average of the neighbours
     *
     * @param data the dataset
     * @param vec1 the data to price
     * @param k    number of neighbours
     * @return the weighted price
     */
    public double weightedKnn(final List<Map<String, List<Double>>> data, final List<Double> vec1, final int k) {
        final List<Pair<Double, Integer>> distances = determineDistances(data, vec1);
        double avg = 0.0;
        double totalWeight = 0.0;

        for (int i = 0; i <= k; i++) {
            double dist = distances.get(i).getValue0();
            int idx = distances.get(i).getValue1();
            double weight = guassianWeight(dist, 5.0);

            avg += weight * data.get(idx - 1).get("result").get(0);
            totalWeight += weight;
        }
        if (totalWeight == 0.0) {
            return 0.0;
        }
        avg = avg / totalWeight;

        return avg;
    }


    /**
     * Gaussian Weight function, smoother weight curve that doesnt go to 0
     *
     * @param distance the distance
     * @param sigma    sigma
     * @return weighted value
     */
    public double guassianWeight(final double distance, final double sigma) {
        double alteredDistance = -(Math.pow(distance, 2));
        double sigmaSize = (2 * Math.pow(sigma, 2));
        return Math.pow(Math.E, (alteredDistance / sigmaSize));
    }

    /**
     * Split the data for cross validation.
     *
     * @param data        the data to split
     * @param testPercent % of data to use for the tests
     * @return a tuple 0 - training, 1 - test
     */
    @SuppressWarnings("unchecked")
    public Pair<List, List> divideData(final List<Map<String, List<Double>>> data, final double testPercent) {
        final List trainingList = new ArrayList();
        final List testList = new ArrayList();

        for (final Map<String, List<Double>> dataItem : data) {
            if (Math.random() < testPercent) {
                testList.add(dataItem);
            } else {
                trainingList.add(dataItem);
            }
        }

        return new Pair(trainingList, testList);
    }


    /**
     * Test result and squares the differences to make it more obvious
     *
     * @param trainingSet the training set
     * @param testSet     the test set
     * @return the error
     */
    @SuppressWarnings("unchecked")
    public double testAlgorithm(final List trainingSet, final List testSet) {
        double error = 0.0;
        final List<Map<String, List<Double>>> typedSet = (List<Map<String, List<Double>>>) testSet;
        for (final Map<String, List<Double>> testData : typedSet) {
            double guess = weightedKnn(trainingSet, testData.get("input"), 3);
            error += Math.pow((testData.get("result").get(0) - guess), 2);
        }
        return error / testSet.size();
    }

    /**
     * This runs iterations of the test, and returns an averaged score
     *
     * @param data        the data
     * @param testPercent % test
     * @param trials      number of iterations
     * @return result
     */
    public double crossValidate(final List<Map<String, List<Double>>> data, final double testPercent, final int trials) {

        double error = 0.0;
        for (int i = 0; i < trials; i++) {
            final Pair<List, List> trainingPair = divideData(data, testPercent);
            error += testAlgorithm(trainingPair.getValue0(), trainingPair.getValue1());
        }
        return error / trials;
    }


    /**
     * Gives the probability that an item is in a price range between 0 and 1
     * Adds up the neighbours weightd and divides it by the total
     *
     * @param data the data
     * @param vec1 the input
     * @param k    the number of neighbours
     * @param low  low amount of range
     * @param high the high amount
     * @return probability between 0 and 1
     */
    public double probabilityGuess(final List<Map<String, List<Double>>> data, final List<Double> vec1, final int k,
                                   final double low, final double high) {

        final List<Pair<Double, Integer>> distances = determineDistances(data, vec1);
        double neighbourWeights = 0.0;
        double totalWeights = 0.0;

        for (int i = 0; i < k; i++) {
            double dist = distances.get(i).getValue0();
            int index = distances.get(i).getValue1();
            double weight = guassianWeight(dist, 5);
            final List<Double> result = data.get(index).get("result");
            double v = result.get(0);

            //check if the point is in the range.
            if (v >= low && v <= high) {
                neighbourWeights += weight;
            }
            totalWeights += weight;
        }
        if (totalWeights == 0) {
            return 0;
        }
        return neighbourWeights / totalWeights;
    }

}
