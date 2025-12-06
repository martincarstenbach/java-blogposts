package com.example.demo.payroll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class HttpRestCallTest {

    @Autowired
    private RestTestClient restClient;

    @Test
    void responseShouldBeOk() throws Exception {

        restClient.get()
                .uri("/employees")
                .exchange()
                .expectStatus().isOk();
    }
}
