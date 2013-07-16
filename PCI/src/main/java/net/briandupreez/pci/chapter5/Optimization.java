package net.briandupreez.pci.chapter5;

import com.google.common.io.Resources;
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
}
