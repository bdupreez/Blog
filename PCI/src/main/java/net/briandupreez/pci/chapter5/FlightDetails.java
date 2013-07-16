package net.briandupreez.pci.chapter5;

import org.joda.time.LocalTime;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/13
 * Time: 10:11 PM
 */
public class FlightDetails {
    public LocalTime depart;
    public LocalTime arrive;
    public double price;

    public FlightDetails(final LocalTime depart, final LocalTime arrive, final double price) {
        this.depart = depart;
        this.arrive = arrive;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightDetails that = (FlightDetails) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (!arrive.equals(that.arrive)) return false;
        if (!depart.equals(that.depart)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = depart.hashCode();
        result = 31 * result + arrive.hashCode();
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FlightDetails{" +
                "depart=" + depart +
                ", arrive=" + arrive +
                ", price=" + price +
                '}';
    }
}
