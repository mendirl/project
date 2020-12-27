package org.acme.reactive.crud;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FruitTest {

    @Test
    void test1() {
        Assertions.assertThat(new Fruit("toto")).isNotNull().hasFieldOrPropertyWithValue("name", "toto");
    }
}
