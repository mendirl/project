package nopackage;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

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
}
