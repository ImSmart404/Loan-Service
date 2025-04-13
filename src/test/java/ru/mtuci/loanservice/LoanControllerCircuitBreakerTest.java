package ru.mtuci.loanservice;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ru.mtuci.loanservice.DTO.ErrorDTO;
import ru.mtuci.loanservice.service.LoanOrderService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoanControllerCircuitBreakerTest extends AbstractITTest {

    private static final String GET_STATUS_ORDER_URL_BASE = "/loan-service/getStatusOrder";

    @Autowired
    private CircuitBreakerRegistry registry;

    @MockBean
    private LoanOrderService loanOrderService;

    @Test
    void circuitBreaker_shouldOpen_afterFailures() throws Exception {
        when(loanOrderService.findByOrderId(any())).thenThrow(new RuntimeException("Test CB exception"));
        CircuitBreaker breaker = registry.circuitBreaker("unstableApiBreaker");

        // Убедимся, что в начале CLOSED
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get(GET_STATUS_ORDER_URL_BASE)
                    .headers(requestHeaders)
                    .param("orderId", "testOrderId")
            );
        }

        // После 10 сбоев — CircuitBreaker должен открыться
        assertThat(breaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        MvcResult mvcResult = mockMvc.perform(get(GET_STATUS_ORDER_URL_BASE)
                .headers(requestHeaders)
                .param("orderId", "testOrderId")
        ).andExpect(status().isInternalServerError()).andReturn();

        assertNotNull(mvcResult.getResponse());
        ErrorDTO errorDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(500, errorDTO.getCode());
        assertEquals("CircuitBreaker 'unstableApiBreaker' is OPEN and does not permit further calls", errorDTO.getMessage());
    }
}
