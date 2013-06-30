package net.briandupreez.pci.chapter4;

import java.util.regex.Pattern;

/**
 * NodeConstants.
 * User: bdupreez
 * Date: 2013/06/23
 * Time: 9:08 AM
 */
public class NodeConstants {
    public static final String URL = "url";
    public static final String WORD = "word";
    public static final String SOURCE = "source";
    public static final String DESTINATION = "destination";
    public static final String INDEX = "index";
    public static final String PAGE_INDEX = "pageIndex";

    public final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
}
