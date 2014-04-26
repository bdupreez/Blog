package net.briandupreez.blog.java8.futures;

import net.briandupreez.blog.java8.futures.example.StringInputTask;
import net.briandupreez.blog.java8.futures.example.StringResults;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test
 * Created by brian on 4/26/14.
 */
public class CompletableFuturesRunnerTest {

    @BeforeClass
    public static void init() {
        BasicConfigurator.configure();
    }

    /**
     *  5tasks at 3000ms concurrently should not be more than 3100
     * @throws Exception error
     */
    @Test(timeout = 3100)
    public void testGo() throws Exception {
        final List<Task<String, String>> taskList = setupTasks();

        final WaitingFuturesRunner<String, String> completableFuturesRunner = new WaitingFuturesRunner<>(taskList, 4, TimeUnit.SECONDS);
        final StringResults consolidatedResults = new StringResults();

        completableFuturesRunner.go("Something To Process", consolidatedResults);

        Assert.assertEquals(5, consolidatedResults.getResults().size());
        for (final String s : consolidatedResults.getResults()) {
            Assert.assertTrue(s.contains("complete"));
            Assert.assertTrue(s.contains("Something To Process"));
        }


    }

    private List<Task<String, String>> setupTasks() {
        final List<Task<String, String>> taskList = new ArrayList<>();
        final StringInputTask stringInputTask = new StringInputTask("Task 1");
        final StringInputTask stringInputTask2 = new StringInputTask("Task 2");
        final StringInputTask stringInputTask3 = new StringInputTask("Task 3");
        final StringInputTask stringInputTask4 = new StringInputTask("Task 4");
        final StringInputTask stringInputTask5 = new StringInputTask("Task 5");
        taskList.add(stringInputTask);
        taskList.add(stringInputTask2);
        taskList.add(stringInputTask3);
        taskList.add(stringInputTask4);
        taskList.add(stringInputTask5);
        return taskList;
    }
}
