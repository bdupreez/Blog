package net.briandupreez.solr.documents;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Representation of a Wikipedia document.
 * Created by Brian on 2014/01/26.
 */
public class WikipediaDocument {

    @Id
    @Field
    private String id;
    @Field
    private String title;
    @Field
    private String user;
    @Field
    private String userId;
    @Field
    private String text;
    @Field
    private Date timestamp;

    private String all;


    public WikipediaDocument() {
    }


    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikipediaDocument that = (WikipediaDocument) o;

        if (all != null ? !all.equals(that.all) : that.all != null) return false;
        if (!id.equals(that.id)) return false;
        if (!text.equals(that.text)) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        if (!title.equals(that.title)) return false;
        if (!userId.equals(that.userId)) return false;
        if (!user.equals(that.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + (all != null ? all.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WikipediaDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", user='" + user + '\'' +
                ", userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
