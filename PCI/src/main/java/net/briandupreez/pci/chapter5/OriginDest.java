package net.briandupreez.pci.chapter5;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/07/13
 * Time: 10:10 PM
 */
public class OriginDest {
    public String origin;
    public String dest;

    public OriginDest(final String origin, final String dest) {
        this.origin = origin;
        this.dest = dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OriginDest that = (OriginDest) o;

        if (!dest.equals(that.dest)) return false;
        if (!origin.equals(that.origin)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + dest.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OriginDest{" +
                "origin='" + origin + '\'' +
                ", dest='" + dest + '\'' +
                '}';
    }
}
