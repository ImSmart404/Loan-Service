package ru.mts.loanservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ru.mts.loanservice.DTO.GetStatusOrderDataResponseDTO;
import ru.mts.loanservice.model.LoanOrder;
import ru.mts.loanservice.model.Tariff;
import ru.mts.loanservice.repository.LoanOrderRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetStatusOrderTest extends AbstractITTest {

    private static final String GET_STATUS_ORDER_URL_BASE = "/loan-service/getStatusOrder";

    @MockBean
    private LoanOrderRepository loanOrderRepository;

    @Test
    void getStatusOrder_shouldReturnOK_withExistingOrder() throws Exception {
        LoanOrder loanOrder = new LoanOrder(1L, "testOrderId", 1L, new Tariff(), 0.05, "Status", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        when(loanOrderRepository.findByOrderId("testOrderId")).thenReturn(Optional.of(loanOrder));

        MvcResult mvcResult = mockMvc.perform(get(GET_STATUS_ORDER_URL_BASE)
                .headers(requestHeaders)
                .param("orderId", "testOrderId")
        ).andExpect(status().isOk()).andReturn();

        GetStatusOrderDataResponseDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GetStatusOrderDataResponseDTO.class);
        assertEquals("Status", response.getStatusOrder());
    }
}
