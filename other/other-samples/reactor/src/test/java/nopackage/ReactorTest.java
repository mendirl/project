package nopackage;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

public class ReactorTest {

    @Test
    public void test1() {
        var elements = new ArrayList<String>();

        Flux.just("1", "2", "3").log().subscribe(elements::add);

        assertThat(elements).containsExactlyInAnyOrder("1", "2", "3");
    }

    @Test
    public void test2() {
        var elements = new ArrayList<String>();

        Flux.just("1", "2", "3").log().subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription sub) {
                sub.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String value) {
                elements.add(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });

        assertThat(elements).containsExactlyInAnyOrder("1", "2", "3");
    }

    @Test
    public void test3() {
        var elements = new ArrayList<String>();

        Flux.just("1", "2", "3").log().subscribe(new Subscriber<>() {
            private Subscription sub;
            int onNextAmount;

            @Override
            public void onSubscribe(Subscription sub) {
                this.sub = sub;
                this.sub.request(2);
            }

            @Override
            public void onNext(String value) {
                elements.add(value);
                onNextAmount++;
                if (onNextAmount % 2 == 0) {
                    sub.request(2);
                }
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });

        assertThat(elements).containsExactlyInAnyOrder("1", "2", "3");
    }

    @Test
    public void test4() {
        var elements = new ArrayList<Integer>();

        Flux.just(1, 2, 3).log().map(i -> i * 2).subscribe(elements::add);

        assertThat(elements).containsExactlyInAnyOrder(4, 2, 6);
    }

    @Test
    public void test5() {
        var elements = new ArrayList<String>();

        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> i * 2)
            .zipWith(Flux.range(0, Integer.MAX_VALUE).log(),
                (one, two) -> String.format("First Flux: %d, Second Flux: %d", one, two))
            .subscribe(elements::add);

        assertThat(elements).containsExactly(
            "First Flux: 2, Second Flux: 0",
            "First Flux: 4, Second Flux: 1",
            "First Flux: 6, Second Flux: 2",
            "First Flux: 8, Second Flux: 3");
    }

    @Test
    public void test6() {
        var publish = Flux.create(fluxSink -> {
            while (true) {
                fluxSink.next(System.currentTimeMillis());
            }
        }).subscribeOn(Schedulers.parallel())
            .sample(ofSeconds(2))
            .log()
            .publish();

        publish.subscribe(System.out::println);
        publish.connect();
    }

    @Test
    public void test7() {
        var elements = new ArrayList<Integer>();

        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> i * 2)
            .subscribeOn(Schedulers.parallel())
            .subscribe(elements::add);
    }

    @Test
    public void test8() {
        var elements = new ArrayList<Integer>();

        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> i * 2)
            .subscribeOn(Schedulers.parallel())
            .subscribe(elements::add);
    }

    @Test
    void exampleColdPublisher() throws InterruptedException {
        // Start a cold Publisher which emits 0,1,2 every sec.
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        // Let's subscribe to that with multiple subscribers.
        flux.subscribe(i -> System.out.println("first_subscriber received value:" + i));
        TimeUnit.SECONDS.sleep(3);
        // Let a second subscriber come after some time 3 secs here.
        flux.subscribe(i -> System.out.println("second_subscriber received value:" + i));
        TimeUnit.SECONDS.sleep(30);
    }

    @Test
    void exampleHotPublisher() throws InterruptedException {
        // Start a cold Publisher which emits 0,1,2 every sec.
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1));
        // Make the Publisher Hot
        ConnectableFlux<Long> connectableFlux = flux.publish();
        // Now that we have a handle on the hot Publisher
        // Let's subscribe to that with multiple subscribers.
        connectableFlux.subscribe(i -> System.out.println("first_subscriber received value:" + i));
        // Start firing events with .connect() on the published flux.
        connectableFlux.connect();
        TimeUnit.SECONDS.sleep(3);
        // Let a second subscriber come after some time 3 secs here.
        connectableFlux.subscribe(i -> System.out.println("second_subscriber received value:" + i));
        TimeUnit.SECONDS.sleep(30);
    }

    // Just simulate some delay on the current thread.
    void delay() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the info about the current code and time
     * This is a generic Example of a Blocking call
     * In reality it can be any Blocking call
     */
    String blockingGetInfo(Integer input) {
        delay();
        return String.format("[%d] on thread [%s] at time [%s]",
            input,
            Thread.currentThread().getName(),
            Instant.now());
    }

    /**
     * Basic example to show that Reactor
     * by itself will run the pipeline or code
     * on the same thread on which .subscribe() happened.
     */
    @Test
    void threadBlocking() {
        Flux.range(1, 5)
            .flatMap(a -> Mono.just(blockingGetInfo(a)))
            .subscribe(System.out::println);
    }

    /**
     * Adding a Scheduler puts the workload
     * of the main thread and hands it over to the
     * Scheduler orchestration.
     * But, still it doesn't gurantee it will leverage
     * all threads of create new threads whenever a
     * Async task is requested. So, basically we wanted
     * to fire all 5 tasks in 5 threads parallely
     * but that will not happen and you will see
     * it will end up reusing the same thread from the Scheduler
     */
    @Test
    void threadNonBlockingBySchedulersProblem() throws InterruptedException {
        Flux.range(1, 5)
            .publishOn(Schedulers.boundedElastic())
            .flatMap(a -> Mono.just(blockingGetInfo(a)))
            .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(20);
    }


    /**
     * Quickfix by converting the Flux Stream
     * to a parallel Flux which would runOn
     * a Scheduler and will be submitted as parallel
     * tasks depending on the .parallel() inputs
     * By default it creates parallism of the same
     * number of cores you have (for me it was 4)
     */
    @Test
    void threadNonBlockingBySchedulers() throws InterruptedException {
        Flux.range(1, 5)
            .parallel()
            .runOn(Schedulers.elastic())
            .flatMap(a -> Mono.just(blockingGetInfo(a)))
            .sequential()
            .subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(20);
    }

    /**
     * Getting into ParallelFlux creates
     * More confusion at times because even if
     * we pass explicit threadpool with more threads
     * it will not use those threads and again
     * will be limited to .parallel() call
     */
    @Test
    public void threadNonBlockingBySchedulersExecutor() {
        ExecutorService myPool = Executors.newFixedThreadPool(10);
        Flux.range(1, 6)
            .parallel()
            .runOn(Schedulers.fromExecutorService(myPool))
            .flatMap(a -> Mono.just(blockingGetInfo(a)))
            .sequential()
            .subscribe(System.out::println);
    }

}
