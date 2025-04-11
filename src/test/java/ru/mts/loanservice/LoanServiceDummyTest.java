package ru.mts.loanservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class LoanServiceDummyTest {

	@LocalServerPort
	Long port;

	@Disabled
	@Test
	void contextLoads() throws InterruptedException {
		log.info(String.valueOf(port));
		Thread.sleep(1000000000);
	}

}
