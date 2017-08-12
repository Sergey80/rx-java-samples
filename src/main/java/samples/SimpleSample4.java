package samples;


import rx.Observable;

import static samples.SimpleSample.*;

// #flatMap and subscribeOn

// Try to make B processed before A, to show that it goes with parallel execution - by using `waitForRandomSeconds`

public class SimpleSample4 {

    public static Observable<String> longOperation(String input) {
        return Observable.fromCallable( () ->
                {
                    waitForRandomSeconds();
                    log("Processed (" + input + ")");
                    return input + " processed";
                }
        );
    }


    public static void main(String[] args) {

        log("Starting");
        final Observable<String> obs = simple();
        log("Created");

        obs.
            subscribeOn(schedulerA).
            doOnNext(x -> log("onNext: " + x)).
            flatMap(x ->
                    longOperation(x).
                    subscribeOn(schedulerC)   // longOperation will use `schedulerC`, that's how we can archive parallelism !
            ).
        observeOn(schedulerB).
        subscribe(
                x -> log("Got " + x),   // So we wait for events to come in Scheduler B, but the events are processing in Scheduler C (long operation)
                Throwable::printStackTrace,
                () -> log("Completed")
        );

    }

}

/*

  41   | main | Starting
  111  | main | Created
  148  | Sched-A-0 | Subscribed
  148  | Sched-A-0 | onNext: A
  172  | Sched-A-0 | onNext: B
  174  | Sched-C-0 | Sleep for 2 seconds
  176  | Sched-C-1 | Sleep for 1 seconds
  1176 | Sched-C-1 | Processed (B)
  1178 | Sched-B-1 | Got B processed
  2174 | Sched-C-0 | Processed (A)
  2175 | Sched-B-2 | Got A processed
  2176 | Sched-B-2 | Completed

 */

