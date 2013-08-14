package net.briandupreez.pci.chapter8;

import org.javatuples.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/05
 * Time: 9:08 PM
 */
public class Optimization {

    public List<Pair<Integer, Integer>> createDomain() {

        final List<Pair<Integer, Integer>> domain = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            final Pair<Integer, Integer> pair = new Pair<>(0, 10);
            domain.add(pair);
        }
        return domain;
    }

    /**
     * Simulated Annealing
     *
     * @param domain list of tuples with min and max
     * @return (global minimum)
     */
    public Double[] simulatedAnnealing(final List<Pair<Integer, Integer>> domain, final double startingTemp, final double cool, final int step) {

        double temp = startingTemp;
        //create random
        Double[] sol = new Double[domain.size()];
        Random random = new Random();
        for (int r = 0; r < domain.size(); r++) {
            sol[r] = Double.valueOf(random.nextInt(19));
        }

        while (temp > 0.1) {
            //pick a random indices
            int i = random.nextInt(domain.size() - 1);

            //pick a directions + or -
            int direction = random.nextInt(step) % 2 == 0 ? -(random.nextInt(step)) : random.nextInt(1);

            Double[] cloneSolr = sol.clone();
            cloneSolr[i] += direction;
            if (cloneSolr[i] < domain.get(i).getValue0()) {
                cloneSolr[i] = Double.valueOf(domain.get(i).getValue0());
            } else if (cloneSolr[i] > domain.get(i).getValue1()) {
                cloneSolr[i] = Double.valueOf(domain.get(i).getValue1());
            }

            //calc current and new cost
            double currentCost = scheduleCost(sol);
            double newCost = scheduleCost(cloneSolr);
            System.out.println("Current: " + currentCost + " New: " + newCost);

            double probability = Math.pow(Math.E, -(newCost - currentCost) / temp);

            // Is it better, or does it make the probability cutoff?
            if (newCost < currentCost || Math.random() < probability) {
                sol = cloneSolr;
            }
            temp = temp * cool;
        }
        return sol;
    }

    public double scheduleCost(Double[] sol) {
        NumPredict numPredict = new NumPredict();
        final List<Map<String,List<Double>>> rescale = numPredict.rescale(numPredict.createWineSet2(), Arrays.asList(sol));
        return numPredict.crossValidate(rescale,0.1,100);
    }

    public Double[] geneticAlgorithm(final List<Pair<Integer, Integer>> domain, final int populationSize,
                                     final int step, final double elite, final int maxIter, final double mutProb) {

        List<Double[]> pop = createPopulation(domain.size(), populationSize);

        final int topElite = new Double(elite * populationSize).intValue();

        final SortedMap<Double, Double[]> scores = new TreeMap<>();
        for (int i = 0; i < maxIter; i++) {
            for (final Double[] run : pop) {
                scores.put(scheduleCost(run), run);
            }
            pop = determineElite(topElite, scores);

            while (pop.size() < populationSize) {

                final Random random = new Random();
                if (Math.random() < mutProb) {

                    final int ran = random.nextInt(topElite);
                    pop.add(mutate(domain, pop.get(ran), step));

                } else {
                    final int ran1 = random.nextInt(topElite);
                    final int ran2 = random.nextInt(topElite);
                    pop.add(crossover(pop.get(ran1), pop.get(ran2), domain.size()));
                }
            }
            System.out.println(scores);
        }

        return scores.entrySet().iterator().next().getValue();


    }

    /**
     * Grab the elites
     *
     * @param topElite how many
     * @param scores   sorted on score
     * @return best ones
     */
    private List<Double[]> determineElite(int topElite, SortedMap<Double, Double[]> scores) {

        Double toKey = null;
        int index = 0;
        for (final Double key : scores.keySet()) {
            if (index++ == topElite) {
                toKey = key;
                break;
            }
        }
        scores = scores.headMap(toKey);
        return new ArrayList<>(scores.values());
    }

    /**
     * Create a population
     *
     * @param arraySize the array size
     * @param popSize   the population size
     * @return a random population
     */
    private List<Double[]> createPopulation(final int arraySize, final int popSize) {
        final List<Double[]> returnList = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            Double[] sol = new Double[arraySize];
            Random random = new Random();
            for (int r = 0; r < arraySize; r++) {
                sol[r] = Double.valueOf(random.nextInt(8));
            }
            returnList.add(sol);
        }
        return returnList;
    }

    /**
     * Mutate a value.
     *
     * @param domain the domain
     * @param vec    the data to be mutated
     * @param step   the step
     * @return mutated array
     */
    private Double[] mutate(final List<Pair<Integer, Integer>> domain, final Double[] vec, final int step) {
        final Random random = new Random();
        int i = random.nextInt(domain.size() - 1);
        Double[] retArr = vec.clone();
        if (Math.random() < 0.5 && (vec[1] - step) > domain.get(i).getValue0()) {
            retArr[i] -= step;
        } else if (vec[i] + step < domain.get(i).getValue1()) {
            retArr[i] += step;
        }
        return vec;
    }

    /**
     * Cross over parts of each array
     *
     * @param arr1 array 1
     * @param arr2 array 2
     * @param max  max value
     * @return new array
     */
    private Double[] crossover(final Double[] arr1, final Double[] arr2, final int max) {
        final Random random = new Random();
        int i = random.nextInt(max);
        return concatArrays(Arrays.copyOfRange(arr1, 0, i), Arrays.copyOfRange(arr2, i, arr2.length));
    }

    /**
     * Concat 2 arrays
     *
     * @param first  first
     * @param second second
     * @return new combined array
     */
    private Double[] concatArrays(final Double[] first, final Double[] second) {
        Double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
