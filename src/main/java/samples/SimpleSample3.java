package samples;


import rx.Observable;

import static samples.SimpleSample.*;

// #Observable.fromCallable, #longOperation(...), #flatMap

public class SimpleSample3 {

    public static Observable<String> longOperation(String input) {
        return Observable.fromCallable( () -> // way to create/(wrap opp to) Observable
                {
                    waitForOneSecond();
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
            flatMap(x -> longOperation(x)). // if we used map() then we would have GotTen: "rx.Observable@4b618a1"
        observeOn(schedulerB).
        subscribe(
                x -> log("Got " + x),
                Throwable::printStackTrace,
                () -> log("Completed")
        );

    }


}

