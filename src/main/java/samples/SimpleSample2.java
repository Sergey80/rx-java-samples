package samples;


import rx.Observable;

import static samples.SimpleSample.*;

// #subscribeOn, #doOnNext, #observeOn

public class SimpleSample2 {


    public static void main(String[] args) {

        log("Starting");                    // "28 | main | Starting" // using main thread from the very beginning
        final Observable<String> obs = simple();
        log("Created");

        obs.
            subscribeOn(schedulerA). // setting default scheduler
            doOnNext(x -> log("onNext: " + x)).     //   "116 | Sched-A0 | onNext: A.."
        observeOn(schedulerB).                       // everything that is after that line will use `schedulerB`
        subscribe(
                x -> log("Got " + x),
                Throwable::printStackTrace,
                () -> log("Completed")
        );

    }

}

// Q
// The interesting part (for me) was is that it performs ALL: Sched-A s, then ALL Sched-B
// I would expect to see something like Sched-A following by Sched-B
// Sow why is that?
// A: Seems by default(?) it processes/handles one scheduler after another

// Q: Also why A is always A-0, and B is always B-1? (why not B-0? Is 0 thread is already taken?? form the pool of 10 threads)

/**
 *
 28  | main | Starting
 94  | main | Created
 123 | Sched-A-0 | Subscribed
 124 | Sched-A-0 | onNext: A
 126 | Sched-A-0 | onNext: B
 126 | Sched-B-1 | Got A
 126 | Sched-B-1 | Got B
 126 | Sched-B-1 | Completed
 */

