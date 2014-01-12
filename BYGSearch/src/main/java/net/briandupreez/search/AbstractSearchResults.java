package net.briandupreez.search;

/**
 * Abstract
 * Created by Brian on 2014/01/04.
 */
public class AbstractSearchResults {
    private final String searchTerm;
    private boolean failed;


    public AbstractSearchResults(final String searchTerm) {
        this.searchTerm = searchTerm;
    }


    public String getSearchTerm() {
        return searchTerm;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return "AbstractSearchResults{" +
                "searchTerm='" + searchTerm + '\'' +
                '}';
    }
}
