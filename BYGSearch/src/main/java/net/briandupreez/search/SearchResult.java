package net.briandupreez.search;

/**
 * Base search result
 * Created by Brian on 2014/01/05.
 */
public class SearchResult {
    private String title;
    private String url;
    private String display;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(final String display) {
        this.display = display;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "\nSearchResult{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", display='" + display + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
