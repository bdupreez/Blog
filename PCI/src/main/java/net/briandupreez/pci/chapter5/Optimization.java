package net.briandupreez.pci.chapter5;

import com.google.common.io.Resources;
import org.javatuples.Pair;
import org.joda.time.LocalTime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/05
 * Time: 9:08 PM
 */
public class Optimization {
    private Map<String, String> people = new LinkedHashMap<>();
    private Map<OriginDest, List<FlightDetails>> flights = new HashMap<>();
    private String destination = "LGA";

    public Optimization() {
        people.put("Seymour", "BOS");
        people.put("Franny", "DAL");
        people.put("Zooey", "CAK");
        people.put("Walt", "MIA");
        people.put("Buddy", "ORD");
        people.put("Les", "OMA");

    }

    public void readFlights() throws IOException {
        final String file = Resources.getResource("schedule.txt").getFile();
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;

        while ((line = reader.readLine()) != null) {
            final String[] lineValues = line.split(",");
            final OriginDest originDest = new OriginDest(lineValues[0], lineValues[1]);

            final String[] timeParts = lineValues[2].split(":");
            final LocalTime depart = new LocalTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));

            final String[] timeParts2 = lineValues[3].split(":");
            final LocalTime arrive = new LocalTime(Integer.parseInt(timeParts2[0]), Integer.parseInt(timeParts2[1]));

            final FlightDetails flightDetails = new FlightDetails(depart, arrive, Double.parseDouble(lineValues[4]));
            List<FlightDetails> flightDetailsList = new ArrayList<>();

            if (flights.containsKey(originDest)) {
                flightDetailsList = flights.get(originDest);

            }
            flightDetailsList.add(flightDetails);
            flights.put(originDest, flightDetailsList);

        }
        System.out.println(flights);

    }

    public void printSchedule(final int... schedule) {
        for (int i = 0; i < schedule.length / 2; ) {

            final List<Map.Entry<String, String>> entryList = new ArrayList<>(people.entrySet());
            final Map.Entry<String, String> person = entryList.get(i);
            final String name = person.getKey();
            final String origin = person.getValue();

            final OriginDest originDest = new OriginDest(origin, destination);

            final FlightDetails flightDetailsGoing = flights.get(originDest).get(schedule[i]);

            final OriginDest destOrigin = new OriginDest(destination, origin);
            final FlightDetails flightDetailsReturning = flights.get(destOrigin).get(schedule[++i]);

            System.out.println(name + "\t\t" + origin + " " + flightDetailsGoing.depart + "-" + flightDetailsGoing.arrive + " $" + flightDetailsGoing.price +
                    " " + flightDetailsReturning.depart + "-" + flightDetailsReturning.arrive + " $" + flightDetailsReturning.price);

        }
    }

    public double scheduleCost(final int... schedule) {
        double totalPrice = 0;
        LocalTime latestArrival = new LocalTime(0, 0);
        LocalTime earliestDep = new LocalTime(23, 59);

        for (int i = 0; i < schedule.length / 2; ) {

            final List<Map.Entry<String, String>> entryList = new ArrayList<>(people.entrySet());
            final Map.Entry<String, String> person = entryList.get(i);
            final String origin = person.getValue();

            final OriginDest originDest = new OriginDest(origin, destination);
            final FlightDetails flightDetailsGoing = flights.get(originDest).get(schedule[i]);

            final OriginDest destOrigin = new OriginDest(destination, origin);
            final FlightDetails flightDetailsReturning = flights.get(destOrigin).get(schedule[++i]);

            totalPrice += flightDetailsGoing.price;
            totalPrice += flightDetailsReturning.price;

            if (flightDetailsGoing.arrive.isAfter(latestArrival)) {
                latestArrival = flightDetailsGoing.arrive;
            }
            if (flightDetailsReturning.depart.isBefore(earliestDep)) {
                earliestDep = flightDetailsReturning.depart;
            }
        }

        //Total wait
        long totalWait = 0;
        for (int i = 0; i < schedule.length / 2; ) {

            final List<Map.Entry<String, String>> entryList = new ArrayList<>(people.entrySet());
            final Map.Entry<String, String> person = entryList.get(i);
            final String origin = person.getValue();

            final OriginDest originDest = new OriginDest(origin, destination);
            final FlightDetails flightDetailsGoing = flights.get(originDest).get(schedule[i]);

            final OriginDest destOrigin = new OriginDest(destination, origin);
            final FlightDetails flightDetailsReturning = flights.get(destOrigin).get(schedule[++i]);


            totalWait += timeDifference(latestArrival.toString(), flightDetailsGoing.arrive.toString());
            totalWait += timeDifference(flightDetailsReturning.depart.toString(), earliestDep.toString());

            if (flightDetailsGoing.arrive.isAfter(latestArrival)) {
                latestArrival = flightDetailsGoing.arrive;
            }
            if (flightDetailsReturning.depart.isBefore(earliestDep)) {
                earliestDep = flightDetailsReturning.depart;
            }
        }

        if (latestArrival.isAfter(earliestDep)) {
            totalPrice += 50;
        }

        return totalPrice + (totalWait / (1000 * 60));
    }

    private long timeDifference(final String firstTime, final String secondTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            final Date date1 = format.parse(firstTime);
            final Date date2 = format.parse(secondTime);
            return date1.getTime() - date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }


    /**
     * Random.
     *
     * @return the best random result
     */
    public int[] randomOptimize() {
        double best = 999999999.0;
        int[] bestr = null;
        for (int i = 0; i < 1000; i++) {
            int[] randomInts = new int[9];
            Random random = new Random();
            for (int r = 0; r < randomInts.length; r++) {
                randomInts[r] = random.nextInt(8);
            }

            final double cost = scheduleCost(randomInts);
            if (cost < best) {
                best = cost;
                bestr = randomInts;
            }

        }
        return bestr;
    }


    /**
     * Hill Climbing
     *
     * @param domain list of tuples with min and max
     * @return the bottom of the hill (local minimum)
     */
    public int[] hillClimb(final List<Pair<Integer, Integer>> domain) {
        //create random
        int[] sol = new int[domain.size()];
        Random random = new Random();
        for (int r = 0; r < domain.size(); r++) {
            sol[r] = random.nextInt(8);
        }

        while (true) {
            double best;

            final List<int[]> neighbours = new ArrayList<>();
            for (int j = 0; j < domain.size(); j++) {
                if (sol[j] > domain.get(j).getValue0() && sol[j] < domain.get(j).getValue1()) {
                    cloneAndAddToIndex(sol, neighbours, j);
                    cloneAndMinusAtIndex(sol, neighbours, j);
                } else if (sol[j] == domain.get(j).getValue0()) {
                    cloneAndAddToIndex(sol, neighbours, j);
                } else if (sol[j] == domain.get(j).getValue1()) {
                    cloneAndMinusAtIndex(sol, neighbours, j);
                }

            }

            double current = scheduleCost(sol);
            best = current;
            for (final int[] neighbour : neighbours) {
                double cost = scheduleCost(neighbour);
                if (cost < best) {
                    best = cost;
                    sol = neighbour;
                }
            }

            if (best == current) {
                break;
            }

        }

        return sol;
    }

    private void cloneAndMinusAtIndex(final int[] sol, final List<int[]> neighbours, final int j) {
        int[] minArr = sol.clone();
        minArr[j] -= 1;
        neighbours.add(minArr);
    }

    private void cloneAndAddToIndex(final int[] sol, final List<int[]> neighbours, final int j) {
        int[] addArr = sol.clone();
        addArr[j] += 1;
        neighbours.add(addArr);
    }

    /**
     * Simulated Annealing
     *
     * @param domain list of tuples with min and max
     * @return (global minimum)
     */
    public int[] simulatedAnealing(final List<Pair<Integer, Integer>> domain, final double startingTemp, final double cool, final int step) {

        double temp = startingTemp;
        //create random
        int[] sol = new int[domain.size()];
        Random random = new Random();
        for (int r = 0; r < domain.size(); r++) {
            sol[r] = random.nextInt(8);
        }

        while (temp > 0.1) {
            //pick a random indices
            int i = random.nextInt(domain.size() - 1);

            //pick a directions + or -
            int direction = random.nextInt(step) % 2 == 0 ? -(random.nextInt(step)) : random.nextInt(1);

            int[] cloneSolr = sol.clone();
            cloneSolr[i] += direction;
            if (cloneSolr[i] < domain.get(i).getValue0()) {
                cloneSolr[i] = domain.get(i).getValue0();
            } else if (cloneSolr[i] > domain.get(i).getValue1()) {
                cloneSolr[i] = domain.get(i).getValue1();
            }

            //calc current and new cost
            double currentCost = scheduleCost(sol);
            double newCost = scheduleCost(cloneSolr);
            System.out.println("Current: " + currentCost + " New: " + newCost);

            double probability = Math.pow(Math.E, -(newCost - currentCost) / temp);

            // Is it better, or does it make the probability cutoff?
            if (newCost < currentCost || random.nextInt() < probability) {
                sol = cloneSolr;
            }

            temp = temp * cool;

        }

        return sol;
    }
}
