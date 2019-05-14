package com.aimlesshammer.pocpapispringboot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTest {
    private final int portNumber = 9090;
    private final String origin = "http://localhost:" + portNumber;
    private final SpringHttpClient springHttpClient = new SpringHttpClient();

    @Before
    public void setUp() {
        PocPapiSpringbootApplication.start(portNumber);
    }

    @After
    public void tearDown() {
        PocPapiSpringbootApplication.stop();
    }

    @Test
    public void respondsWithHelloWorldOnRootResource() {
        assertEquals("Hello World! :)", springHttpClient.get(origin + "/greeting"));
    }
}
