package ru.mts.loanservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mts.loanservice.DTO.ErrorDTO;
import ru.mts.loanservice.DTO.GetTariffsDataResponseDTO;
import ru.mts.loanservice.DTO.ResponseDataDTO;
import ru.mts.loanservice.DTO.ResponseErrorDTO;
import ru.mts.loanservice.controller.LoanController;
import ru.mts.loanservice.model.*;
import ru.mts.loanservice.service.LoanOrderService;
import ru.mts.loanservice.service.TariffService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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

        GetTariffsDataResponseDTO data = new GetTariffsDataResponseDTO(tariffs);
        ResponseDataDTO response = new ResponseDataDTO(data);

        when(tariffService.findAll()).thenReturn(ResponseEntity.ok(response));
        mockMvc.perform(get("/loan-service/getTariffs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testControllerGetStatusOrderShouldReturnBadRequestWhenOrderNotFound() throws Exception {

        ErrorDTO errorDTO = new ErrorDTO("ORDER_NOT_FOUND", "Заявка не найдена");
        ResponseErrorDTO response = new ResponseErrorDTO(errorDTO);

        when(loanOrderService.findByOrderId(Mockito.anyString())).thenReturn(ResponseEntity.badRequest().body(response));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/loan-service/getStatusOrder")
                        .param("orderId", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseErrorDTO responseErrorDTO = objectMapper.readValue(content, ResponseErrorDTO.class);

        ErrorDTO expectedErrorDTO = new ErrorDTO("ORDER_NOT_FOUND", "Заявка не найдена");
        ResponseErrorDTO expectedResponse = new ResponseErrorDTO(expectedErrorDTO);

        assert(expectedResponse.equals(responseErrorDTO));
    }


   @Test
   public void testControllerPostOrderWhenStatusInProgress() throws Exception{
        ErrorDTO inProgressErrorDTO = new ErrorDTO("LOAN_CONSIDERATION", "Тариф в процессе рассмотрения");
        ResponseErrorDTO inProgressResponse = new ResponseErrorDTO(inProgressErrorDTO);

        when(loanOrderService.findByUserId(123L, 123L)).thenReturn( ResponseEntity.status(HttpStatus.BAD_REQUEST).body(inProgressResponse));
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
       ResponseErrorDTO responseErrorDTO = objectMapper.readValue(content, ResponseErrorDTO.class);

       ErrorDTO expectedErrorDTO = new ErrorDTO("LOAN_CONSIDERATION", "Тариф в процессе рассмотрения");
       ResponseErrorDTO expectedResponse = new ResponseErrorDTO(expectedErrorDTO);

       assert(expectedResponse.equals(responseErrorDTO));
   }

}
