package net.briandupreez.blog.java8.futures.example;

import net.briandupreez.blog.java8.futures.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Example Task
 */
public class StringInputTask implements Task<String, String> {
    private transient static final Log LOG = LogFactory.getLog(StringInputTask.class);
    private final String taskName;

    public StringInputTask(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String process(final String input, final CountDownLatch latch) {

        //Some process that takes time
        try {
            Thread.sleep(3000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        LOG.debug("Done: " + taskName);
        latch.countDown();
        return  String.format("Task %s - %s - complete - Yay!", taskName, input);
    }

    @Override
    public String toString() {
        return "StringInputTask{" +
                "taskName='" + taskName + '\'' +
                '}';
    }
}
