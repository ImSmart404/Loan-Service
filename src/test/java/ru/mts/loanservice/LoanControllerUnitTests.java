package ru.mts.loanservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mts.loanservice.controller.LoanController;
import ru.mts.loanservice.model.LoanOrder;
import ru.mts.loanservice.model.Tariff;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@WebMvcTest(LoanController.class)
public class LoanControllerUnitTests {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TariffService tariffService;

    @MockBean
    LoanOrderService loanOrderService;

    @Test
    public void testControllerGetTariffsShouldReturnStatus200() throws Exception {
        List<Tariff> tariffs = Arrays.asList(
                new Tariff(null,"Tariff 1", "10.0"),
                new Tariff( null, "Tariff 2", "20.0")
        );

        Mockito.when(tariffService.findAll()).thenReturn(tariffs);
        mockMvc.perform(get("/loan-service/getTariffs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testControllerGetStatusOrderShouldReturnBadRequestWhenOrderNotFound() throws Exception {
        Mockito.when(loanOrderService.findByOrderId(anyString())).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/loan-service/getStatusOrder")
                .param("orderId", "12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> response = objectMapper.readValue(content, new TypeReference<HashMap<String, Object>>(){});

        HashMap<String, Object> errorMap = new HashMap<>();
        errorMap.put("code", "ORDER_NOT_FOUND");
        errorMap.put("message", "Заявка не найдена");

        HashMap<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("error", errorMap);
        assert(response.equals(expectedResponse));
    }

    @Test
    public void testControllerGetStatusOrderShouldReturnOkRequestForExistingOrder() throws Exception{
        LoanOrder newOrder = newOrder();
        Mockito.when(loanOrderService.findByOrderId(newOrder.getOrderId())).thenReturn(Optional.of(newOrder));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/loan-service/getStatusOrder")
                .param("orderId", newOrder.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> response = objectMapper.readValue(content, new TypeReference<HashMap<String, Object>>(){});

        HashMap<String,Object> expectedResponse = new HashMap<>();
        expectedResponse.put("data", Collections.singletonMap("orderStatus", "IN_PROGRESS"));

        assert (response.equals(expectedResponse));
    }

   @Test
   public void testControllerPostOrderWhenStatusInProgress() throws Exception{
        List<LoanOrder> loanOrder = List.of(newOrder());
        Mockito.when(loanOrderService.findByUserId(123L)).thenReturn(loanOrder);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Long> requestMap = new HashMap<>();
        requestMap.put("tariffId", 123L);
        requestMap.put("userId", 123L);
        MvcResult result =  mockMvc.perform(post("/loan-service/order")
                .content(objectMapper.writeValueAsString(requestMap))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        HashMap<String, Object> response = objectMapper.readValue(content,new TypeReference<HashMap<String, Object>>(){});

        HashMap<String,String> dataMap = new HashMap<>();
        HashMap<String,Object> okMap = new HashMap<>();
        dataMap.put("code", "LOAN_CONSIDERATION");
        dataMap.put("message", "Тариф в процессе рассмотрения");
        okMap.put("data", dataMap);

        assert(response.equals(okMap));
   }

   public LoanOrder newOrder(){
       Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
       UUID uuid = UUID.randomUUID();
       LoanOrder newOrder = new LoanOrder();
       newOrder.setId(123L);
       newOrder.setOrderId(String.valueOf(uuid));
       newOrder.setUserId(123L);
       newOrder.setTariff(new Tariff(123L,"Тариф 1", "10.9"));
       newOrder.setCreditRating(1);
       newOrder.setStatus("IN_PROGRESS");
       newOrder.setTimeUpdate(timestamp);
       newOrder.setTimeInsert(timestamp);
       return newOrder;
   }
}
