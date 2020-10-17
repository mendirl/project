package nopackage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomServiceTest {


    @Test
    public void test1() {
        var sut = new RandomService();

        Assertions.assertThat(sut.value()).isNotNull();

    }
}
