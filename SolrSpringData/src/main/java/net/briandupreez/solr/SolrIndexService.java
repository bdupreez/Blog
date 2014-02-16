package net.briandupreez.solr;

/**
 * Base Index Service
 * Created by Brian on 2014/01/26.
 *
 * @param <T>  the type
 * @param <ID> the id to be used.
 */
public interface SolrIndexService<T, ID> {

    T add(final T entry);

    void delete(final ID id);

}
