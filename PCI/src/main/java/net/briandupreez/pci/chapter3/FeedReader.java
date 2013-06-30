package net.briandupreez.pci.chapter3;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;

public class FeedReader {


    @SuppressWarnings("unchecked")
    public static Set<String> determineAllUniqueWords(final String url, final Set<String> blogWordList) {

        boolean ok = false;
        try {
            URL feedUrl = new URL(url);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            final List<SyndEntryImpl> entries = feed.getEntries();
            for (final SyndEntryImpl entry : entries) {
                blogWordList.addAll(cleanAndSplitString(entry.getTitle()));
                blogWordList.addAll(doCategories(entry));
                blogWordList.addAll(doDescription(entry));
                blogWordList.addAll(doContent(entry));
            }

            ok = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + url + "\n" + ex.getMessage());
        }

        if (!ok) {
            System.out.println("FeedReader reads and prints any RSS/Atom feed type.");
            System.out.println("The first parameter must be the URL of the feed to read.");
        }

        return blogWordList;

    }

    @SuppressWarnings("unchecked")
    private static List<String> doContent(final SyndEntryImpl entry) {
        List<String> blogWordList = new ArrayList<>();
        final List<SyndContent> contents = entry.getContents();
        if (contents != null) {
            for (final SyndContent syndContent : contents) {
                if ("text/html".equals(syndContent.getMode())) {
                    blogWordList.addAll(stripHtmlAndAddText(syndContent));
                } else {
                    blogWordList.addAll(cleanAndSplitString(syndContent.getValue()));
                }
            }
        }
        return blogWordList;
    }

    private static List<String> doDescription(final SyndEntryImpl entry) {
        final List<String> blogWordList = new ArrayList<>();
        final SyndContent description = entry.getDescription();
        if (description != null) {
            if ("text/html".equals(description.getType())) {
                blogWordList.addAll(stripHtmlAndAddText(description));
            } else {
                blogWordList.addAll(cleanAndSplitString(description.getValue()));
            }
        }
        return blogWordList;
    }

    @SuppressWarnings("unchecked")
    private static List<String> doCategories(final SyndEntryImpl entry) {
        final List<String> blogWordList = new ArrayList<>();
        final List<SyndCategoryImpl> categories = entry.getCategories();
        for (final SyndCategoryImpl category : categories) {
            blogWordList.add(category.getName().toLowerCase());
        }
        return blogWordList;
    }


    private static List<String> stripHtmlAndAddText(final SyndContent description) {
        String html = description.getValue();
        Document document = Jsoup.parse(html);
        Elements elements = document.getAllElements();
        final List<String> allWords = new ArrayList<>();
        for (final Element element : elements) {
            allWords.addAll(cleanAndSplitString(element.text()));
        }
        return allWords;
    }

    private static List<String> cleanAndSplitString(final String input) {
        if (input != null) {
            final String[] dic = input.toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("\\p{Digit}", "").split("\\s+");
            return Arrays.asList(dic);
        }
        return new ArrayList<>();

    }

    @SuppressWarnings("unchecked")
    public static Map<String, Double> countWords(final String url, final Set<String> blogWords) {

        final Map<String, Double> resultMap = new TreeMap<>();
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            final List<SyndEntryImpl> entries = feed.getEntries();
            final List<String> allBlogWords = new ArrayList<>();
            for (final SyndEntryImpl entry : entries) {
                allBlogWords.addAll(cleanAndSplitString(entry.getTitle()));
                allBlogWords.addAll(doCategories(entry));
                allBlogWords.addAll(doDescription(entry));
                allBlogWords.addAll(doContent(entry));
            }
            for (final String word : blogWords) {
                resultMap.put(word, (double) Collections2.filter(allBlogWords, Predicates.equalTo(word)).size());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + url + "\n" + ex.getMessage());
        }


        return resultMap;
    }
}
