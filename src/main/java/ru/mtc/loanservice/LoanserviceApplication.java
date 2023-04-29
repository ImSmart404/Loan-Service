package ru.mtc.loanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoanserviceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(LoanserviceApplication.class, args);
		LoanOrderScheduler scheduler = context.getBean(LoanOrderScheduler.class);
		scheduler.updateInProgressOrders();

	}

}
