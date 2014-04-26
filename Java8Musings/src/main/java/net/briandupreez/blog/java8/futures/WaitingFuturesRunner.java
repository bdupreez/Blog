package net.briandupreez.blog.java8.futures;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * Generified future running and completion
 *
 * @param <T> the result type
 * @param <S> the task input
 */
public class WaitingFuturesRunner<T, S> {
    private transient static final Log logger = LogFactory.getLog(WaitingFuturesRunner.class);
    private final Collection<Task<T, S>> tasks;
    private final long timeOut;
    private final TimeUnit timeUnit;
    private final ExecutorService executor;

    /**
     * Constructor, used to initialise with the required tasks
     *
     * @param tasks the list of tasks to execute
     * @param timeOut  max length of time to wait
     * @param timeUnit     time out timeUnit
     */
    public WaitingFuturesRunner(final Collection<Task<T, S>> tasks, final long timeOut, final TimeUnit timeUnit) {
        this.tasks = tasks;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.executor = Executors.newFixedThreadPool(tasks.size());
    }

    /**
     * Go!
     *
     * @param taskInput          The input to the task
     * @param consolidatedResult a container of all the completed results
     */
    public void go(final S taskInput, final ConsolidatedResult<T> consolidatedResult) {
        final CountDownLatch latch = new CountDownLatch(tasks.size());
        final List<CompletableFuture<T>> theFutures = tasks.stream()
                .map(aSearch -> CompletableFuture.supplyAsync(() -> processTask(aSearch, taskInput, latch), executor))
                .collect(Collectors.<CompletableFuture<T>>toList());

        final CompletableFuture<List<T>> allDone = collectTasks(theFutures);
        try {
            latch.await(timeOut, timeUnit);
            logger.debug("complete... adding results");
            allDone.get().forEach(consolidatedResult::addResult);
        } catch (final InterruptedException | ExecutionException e) {
            logger.error("Thread Error", e);
            throw new RuntimeException("Thread Error, could not complete processing", e);
        }
    }

    private <E> CompletableFuture<List<E>> collectTasks(final List<CompletableFuture<E>> futures) {
        final CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v -> futures.stream()
                        .map(CompletableFuture<E>::join)
                        .collect(Collectors.<E>toList())
        );
    }

    private T processTask(final Task<T, S> task, final S searchTerm, final CountDownLatch latch) {
        logger.debug("Starting: " + task);
        T searchResults = null;
        try {
            searchResults = task.process(searchTerm, latch);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }

}
