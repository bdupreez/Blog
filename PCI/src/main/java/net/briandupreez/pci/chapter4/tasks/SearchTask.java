package net.briandupreez.pci.chapter4.tasks;

import net.briandupreez.pci.chapter4.CreateDBFactory;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Created with IntelliJ IDEA.
 * User: bdupreez
 * Date: 2013/06/24
 * Time: 9:12 PM
 */
public class SearchTask {
    protected final String[] searchTerms;
    protected GraphDatabaseService graphDb = CreateDBFactory.createInMemoryDB();

    /**
     * Constructor
     * @param terms words to chapter4 for
     */
    public SearchTask(final String... terms) {
        this.searchTerms = terms;
    }

    protected ExecutionResult executeQuery(final String... words) {
        final ExecutionEngine engine = new ExecutionEngine(graphDb);
        final StringBuilder bob = new StringBuilder("START page=node(*) MATCH (page)-[:CONTAINS]->words ");
        bob.append("WHERE words.word in [");
        bob.append(formatArray(words));
        bob.append("] ");
        bob.append("RETURN DISTINCT page, words");

        return engine.execute(bob.toString());
    }

    /**
      * Format array of strings for in statement
      *
      * @param args
      * @return
      */
     protected String formatArray(final String... args) {
         final String format = new String(new char[args.length]).replace("\0", "'%s',");
         final String result = String.format(format, args);

         return result.substring(0, result.length() - 1);
     }
}
