package core.project.library;

import core.project.library.application.bootstrap.Bootstrap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BootstrapTest {

    @Autowired
    Bootstrap bootstrap;

    @RepeatedTest(125)
    void repeatedTest() {

        Assertions.assertThatNoException().isThrownBy(() -> bootstrap.run());

    }


}
