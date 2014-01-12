package net.briandupreez.search.google;


import net.briandupreez.search.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * Google specific return
 * Created by Brian on 2014/01/05.
 */
public class GoogleSearchResult extends SearchResult {

    private String fileFormat;
    private String formattedUrl;
    private String htmlSnippet;
    private String htmlTitle;
    //private Image image;
    private String kind;
    private String link;
    private String mime;
    private Map<String, List<Map<String, Object>>> pagemap;

    public Map<String, List<Map<String, Object>>> getPagemap() {
        return pagemap;
    }

    public void setPagemap(final Map<String, List<Map<String, Object>>> pagemap) {
        this.pagemap = pagemap;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(final String mime) {
        this.mime = mime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(final String kind) {
        this.kind = kind;
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(final String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public void setHtmlSnippet(final String htmlSnippet) {
        this.htmlSnippet = htmlSnippet;
    }

    public String getFormattedUrl() {
        return formattedUrl;
    }

    public void setFormattedUrl(final String formattedUrl) {
        this.formattedUrl = formattedUrl;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(final String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return "GoogleSearchResult{" +
                "fileFormat='" + fileFormat + '\'' +
                ", formattedUrl='" + formattedUrl + '\'' +
                ", htmlSnippet='" + htmlSnippet + '\'' +
                ", htmlTitle='" + htmlTitle + '\'' +
                ", kind='" + kind + '\'' +
                ", link='" + link + '\'' +
                ", mime='" + mime + '\'' +
                ", pagemap=" + pagemap +
                '}';
    }
}
