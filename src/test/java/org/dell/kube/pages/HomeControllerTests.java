package org.dell.kube.pages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeControllerTests {
<<<<<<< HEAD
    private final String message = "Hello Yellow Pages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController(message);
=======
    private final String message = "YellowPages";

    @Test
    public void itSaysYellowPagesHello() throws Exception {
        HomeController controller = new HomeController();
>>>>>>> master

        assertThat(controller.getPage()).contains(message);
    }


<<<<<<< HEAD
}
=======
}
>>>>>>> master
