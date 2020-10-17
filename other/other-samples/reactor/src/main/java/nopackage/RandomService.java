package nopackage;

import java.util.Random;

public class RandomService {

    public Integer value() {
        return new Random().nextInt();
    }
}
