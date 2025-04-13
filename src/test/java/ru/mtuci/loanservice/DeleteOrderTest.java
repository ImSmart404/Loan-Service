package ru.mtuci.loanservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.mtuci.loanservice.DTO.DeleteOrderDTO;
import ru.mtuci.loanservice.DTO.ErrorDTO;
import ru.mtuci.loanservice.model.LoanOrder;
import ru.mtuci.loanservice.model.Tariff;
import ru.mtuci.loanservice.repository.LoanOrderRepository;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteOrderTest extends AbstractITTest {
    private static final String DELETE_ORDER_URL = "/loan-service/deleteOrder";

    @Autowired
    private LoanOrderRepository loanOrderRepository;

    @AfterEach
    public void tearDown() {
        loanOrderRepository.deleteAll();
    }

    @Test
    void deleteOrder_shouldReturnOK_forExistedOrder() throws Exception {

        Long userId = 1L;
        String orderId = "testOrderId1";

        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("userId", userId.toString());
        requestParameters.put("orderId", orderId);

        createAndSaveOrder(userId, orderId, "IN_PROGRESS");

        //then
        MvcResult mvcResult = mockMvc.perform(delete(DELETE_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse());
        DeleteOrderDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DeleteOrderDTO.class);
        assertEquals("Заявка успешно удалена", response.getMessage());
    }

    @Test
    void deleteOrder_shouldReturnBadRequest_forExistedOrderWithUndeletableStatus() throws Exception {

        Long userId = 2L;
        String orderId = "testOrderId2";

        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("userId", userId.toString());
        requestParameters.put("orderId", orderId);

        createAndSaveOrder(userId, orderId, "SUCCESS");

        //then
        MvcResult mvcResult = mockMvc.perform(delete(DELETE_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(mvcResult.getResponse());
        ErrorDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(400, response.getCode());
        assertEquals("Невозможно удалить заявку", response.getMessage());
    }


    private void createAndSaveOrder(Long userId, String orderId, String status) {
        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setType("Test type");
        tariff.setInterestRate("0.55");

        LoanOrder testLoanOrder = new LoanOrder();
        testLoanOrder.setOrderId(orderId);
        testLoanOrder.setUserId(userId);
        testLoanOrder.setStatus(status);
        testLoanOrder.setInsertTime(Timestamp.valueOf(LocalDateTime.now()));
        testLoanOrder.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        testLoanOrder.setTariff(tariff);

        loanOrderRepository.save(testLoanOrder);
    }
}
