package samples;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newFixedThreadPool;

// #Observable.create, #scheduler


// A base util functionality that we will use over the next samples. The initial base setup.

public class SimpleSample {

    static long before = System.currentTimeMillis();

    public static void main(String[] args) {

        log("Starting");
        final Observable<String> obs = simple();
        log("Created");

        obs.subscribe(
                x -> log("Got " + x),
                Throwable::printStackTrace,
                () -> log("Completed")
        );

    }

    // simple data to stream
    static public Observable<String> simple() {
        return Observable.create(subscriber -> {
            log("Subscribed");
            subscriber.onNext("A");
            subscriber.onNext("B");
            subscriber.onCompleted();
        });
    }

    // nice logging (important to see which thread are in use)
    static void log(String line) {
        System.out.println(
                System.currentTimeMillis() - before + " | " +
                Thread.currentThread().getName()    + " | " +
                line
        );
    }

    // -- some predefined schedulers
    // It make sense to name them with meaningful names (best practices)
    private static ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched-A-%d"));
    public static Scheduler schedulerA = Schedulers.from(poolA);

    private static ExecutorService poolB = newFixedThreadPool(10, threadFactory("Sched-B-%d"));
    public static Scheduler schedulerB = Schedulers.from(poolB);

    private static ExecutorService poolC = newFixedThreadPool(10, threadFactory("Sched-C-%d"));
    public static Scheduler schedulerC = Schedulers.from(poolC);

    static private ThreadFactory threadFactory(String pattern) {
        // uses google guava builder
        return new ThreadFactoryBuilder().setNameFormat(pattern).build();
    }

    // --- some predefined sleeps

    public static void waitForOneSecond() {
        try {
            log("Sleep...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForRandomSeconds() {
        try {
            int delay = ( new Random().nextInt(2) + 1) * 1000; // 1 or 2 second
            log("Sleep for " + delay/1000 + " seconds");
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

/**
   0 | main | Starting
 111 | main | Created
 123 | main | Subscribed
 123 | main | Got A
 123 | main | Got B
 123 | main | Completed
 */
