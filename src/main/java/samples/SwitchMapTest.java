package samples;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class SwitchMapTest {

    private static ExecutorService poolC = newFixedThreadPool(10, threadFactory("Sched-C-%d"));
    public static Scheduler schedulerC = Schedulers.from(poolC);

    static long before = System.currentTimeMillis();

    public static void main(String[] args) {

        log("Starting");
        final Observable<String> obs = getSimpleData();
        log("Created");

        obs
//        .flatMap(x -> 
//            longOperation(x).subscribeOn(schedulerC)
//        )
        .switchMap(x ->                                   // comment switchMap, uncomment the flatMap to see the difference
            longOperation(x).subscribeOn(schedulerC)
        )
        .subscribe(
            x -> log("Got " + x),
            Throwable::printStackTrace,
            () -> log("Completed")
        );;

    }

    public static Observable<String> longOperation(String input) {
        return Observable.fromCallable( () ->
            {
                log("Going to emmit: " + input);
                long gotToWait = waitForRandomSeconds(input);
                log("Emitted (" + input + ") in " + gotToWait + " milliseconds");
                return input + " processed";
            }
        );
    }

    static public Observable<String> getSimpleData() {
        return Observable.create( subscriber -> {
            log("Subscribed");
            subscriber.onNext("A");
            subscriber.onNext("B");
            subscriber.onCompleted();
        });
    }

    public static long waitForRandomSeconds(String value) {
        try {
            long delay = ( new Random().nextInt(2) + 1) * 1000; // 1 or 2 second
            log("Sleep for " + delay/1000 + " seconds for "  + value);
            Thread.sleep(delay);
            return delay;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void log(String line) {
        System.out.println(
            System.currentTimeMillis() - before + " | " +
                Thread.currentThread().getName()    + " | " +
                line
        );
    }

    static private ThreadFactory threadFactory(String pattern) {
        // uses google guava builder
        return new ThreadFactoryBuilder().setNameFormat(pattern).build();
    }

}

/*
0 | main | Starting
84 | main | Created
103 | main | Subscribed
118 | Sched-C-0 | Going to emmit: A
119 | Sched-C-1 | Going to emmit: B
119 | Sched-C-0 | Sleep for 1 seconds for A
119 | Sched-C-1 | Sleep for 2 seconds for B
1123 | Sched-C-0 | Emitted (A) in 1000 milliseconds
2122 | Sched-C-1 | Emitted (B) in 2000 milliseconds
2128 | Sched-C-1 | Got B processed
2128 | Sched-C-1 | Completed
*/
