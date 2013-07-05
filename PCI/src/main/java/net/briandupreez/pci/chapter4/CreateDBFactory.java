package net.briandupreez.pci.chapter4;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Double check locking Singleton
 * User: bdupreez
 * Date: 2013/06/18
 * Time: 11:38 AM
 */
public class CreateDBFactory {

    private static GraphDatabaseService graphDb = null;
    public static final String RESOURCES_CRAWL_DB = "resources/crawl/db";

    public static GraphDatabaseService createInMemoryDB() {
        if (null == graphDb) {
            synchronized (GraphDatabaseService.class) {
                if (null == graphDb) {
                    final Map<String, String> config = new HashMap<>();
                    config.put("neostore.nodestore.db.mapped_memory", "50M");
                    config.put("string_block_size", "60");
                    config.put("array_block_size", "300");
                    graphDb = new GraphDatabaseFactory()
                            .newEmbeddedDatabaseBuilder(RESOURCES_CRAWL_DB)
                            .setConfig(config)
                            .newGraphDatabase();

                    registerShutdownHook(graphDb);
                }
            }
        }
        return graphDb;
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }


    public static void clearDb() {
        try {
            if(graphDb != null){
                graphDb.shutdown();
                graphDb = null;
            }
            FileUtils.deleteRecursively(new File(RESOURCES_CRAWL_DB));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
