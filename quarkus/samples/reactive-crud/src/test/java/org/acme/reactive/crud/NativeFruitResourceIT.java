package org.acme.reactive.crud;

import io.quarkus.test.junit.NativeImageTest;
import org.junit.jupiter.api.Disabled;

@NativeImageTest
@Disabled
public class NativeFruitResourceIT extends FruitResourceTest {

    // Execute the same tests but in native mode.
}
