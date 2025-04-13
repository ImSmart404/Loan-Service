package ru.mtuci.loanservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.mtuci.loanservice.DTO.ErrorDTO;
import ru.mtuci.loanservice.DTO.PostOrderIdDataResponseDTO;
import ru.mtuci.loanservice.model.LoanOrder;
import ru.mtuci.loanservice.model.Tariff;
import ru.mtuci.loanservice.repository.LoanOrderRepository;
import ru.mtuci.loanservice.repository.TariffRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateOrderTest extends AbstractITTest{
    private static final String GET_STATUS_ORDER_URL = "/loan-service/order";

    @MockBean
    private TariffRepository tariffRepository;

    @MockBean
    private LoanOrderRepository loanOrderRepository;

    @Test
    void postOrder_shouldReturnOK_withExistingOrder() throws Exception {
        Long testUserId = 1L;
        Long testTariffId = 1L;

        List<LoanOrder> testLoanOrders = createTestLoanOrder("SUCCEED");
        Tariff tariff = testLoanOrders.get(0).getTariff();

        //when
        when(tariffRepository.isTariffExistsById(testTariffId)).thenReturn(Boolean.TRUE);
        when(loanOrderRepository.findByUserId(testUserId)).thenReturn(testLoanOrders);
        when(tariffRepository.findById(testTariffId)).thenReturn(Optional.ofNullable(tariff));

        Map<String, Long> requestParameters = new HashMap<>();
        requestParameters.put("tariffId", testTariffId);
        requestParameters.put("userId", testUserId);

        //then
        MvcResult mvcResult = mockMvc.perform(post(GET_STATUS_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse());

        PostOrderIdDataResponseDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PostOrderIdDataResponseDTO.class);
        assertNotNull(response.getOrderId());

        //assert that order was saved
        verify(loanOrderRepository, times(1)).save(any());
    }

    @Test
    void postOrder_shouldReturnBadRequest_withInProgressOrderStatus() throws Exception {
        Long testUserId = 1L;
        Long testTariffId = 1L;

        List<LoanOrder> testLoanOrders = createTestLoanOrder("IN_PROGRESS");
        Tariff tariff = testLoanOrders.get(0).getTariff();

        //when
        when(tariffRepository.isTariffExistsById(testTariffId)).thenReturn(Boolean.TRUE);
        when(loanOrderRepository.findByUserId(testUserId)).thenReturn(testLoanOrders);
        when(tariffRepository.findById(testTariffId)).thenReturn(Optional.ofNullable(tariff));

        Map<String, Long> requestParameters = new HashMap<>();
        requestParameters.put("tariffId", testTariffId);
        requestParameters.put("userId", testUserId);

        //then
        MvcResult mvcResult = mockMvc.perform(post(GET_STATUS_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(mvcResult.getResponse());

        ErrorDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(400, response.getCode());
        assertEquals("Тариф в процессе рассмотрения", response.getMessage());

        //assert that order was saved
        verify(loanOrderRepository, never()).save(any());
    }

    @Test
    void postOrder_shouldReturnBadRequest_withApprovedOrderStatus() throws Exception {
        Long testUserId = 1L;
        Long testTariffId = 1L;

        List<LoanOrder> testLoanOrders = createTestLoanOrder("APPROVED");
        Tariff tariff = testLoanOrders.get(0).getTariff();

        //when
        when(tariffRepository.isTariffExistsById(testTariffId)).thenReturn(Boolean.TRUE);
        when(loanOrderRepository.findByUserId(testUserId)).thenReturn(testLoanOrders);
        when(tariffRepository.findById(testTariffId)).thenReturn(Optional.ofNullable(tariff));

        Map<String, Long> requestParameters = new HashMap<>();
        requestParameters.put("tariffId", testTariffId);
        requestParameters.put("userId", testUserId);

        //then
        MvcResult mvcResult = mockMvc.perform(post(GET_STATUS_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(mvcResult.getResponse());

        ErrorDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(400, response.getCode());
        assertEquals("Тариф уже одобрен", response.getMessage());

        //assert that order was saved
        verify(loanOrderRepository, never()).save(any());
    }

    @Test
    void postOrder_shouldReturnBadRequest_withRefusedOrderStatus() throws Exception {
        Long testUserId = 1L;
        Long testTariffId = 1L;

        List<LoanOrder> testLoanOrders = createTestLoanOrder("REFUSED");
        Tariff tariff = testLoanOrders.get(0).getTariff();

        //when
        when(tariffRepository.isTariffExistsById(testTariffId)).thenReturn(Boolean.TRUE);
        when(loanOrderRepository.findByUserId(testUserId)).thenReturn(testLoanOrders);
        when(tariffRepository.findById(testTariffId)).thenReturn(Optional.ofNullable(tariff));

        Map<String, Long> requestParameters = new HashMap<>();
        requestParameters.put("tariffId", testTariffId);
        requestParameters.put("userId", testUserId);

        //then
        MvcResult mvcResult = mockMvc.perform(post(GET_STATUS_ORDER_URL)
                        .headers(requestHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestParameters)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(mvcResult.getResponse());

        ErrorDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(400, response.getCode());
        assertEquals("Попробуйте оставить заявку позже", response.getMessage());

        //assert that order was saved
        verify(loanOrderRepository, never()).save(any());
    }

    private List<LoanOrder> createTestLoanOrder(String status) {
        List<LoanOrder> loanOrders = new ArrayList<>();

        Tariff tariff = new Tariff();
        tariff.setId(1L);
        tariff.setType("Test type");
        tariff.setInterestRate("0.55");

        LoanOrder loanOrder = new LoanOrder();
        loanOrder.setTariff(tariff);
        loanOrder.setOrderId("1");
        loanOrder.setInsertTime(Timestamp.valueOf(LocalDateTime.now()));
        loanOrder.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        loanOrder.setUserId(1L);
        loanOrder.setStatus(status);

        loanOrders.add(loanOrder);
        return loanOrders;
    }
}
