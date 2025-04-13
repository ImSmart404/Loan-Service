package ru.mts.loanservice;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ru.mts.loanservice.DTO.ErrorDTO;
import ru.mts.loanservice.DTO.GetTariffsDataResponseDTO;
import ru.mts.loanservice.model.Tariff;
import ru.mts.loanservice.repository.TariffRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetTarrifsTest extends AbstractITTest {

    private static final String GET_TARRIFS_URL_BASE = "/loan-service/getTariffs";

    @MockBean
    private TariffRepository tariffRepository;

    @Test
    void getTariffs_shouldReturnOK() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(get(GET_TARRIFS_URL_BASE)
                .headers(requestHeaders)
        ).andExpect(status().isOk()).andReturn();

        //then
        GetTariffsDataResponseDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GetTariffsDataResponseDTO.class);
        List<Tariff> tariffs = response.getTariffs();

        assertEquals(3, tariffs.size(), "Incorrect size of returned tariff list");

        Tariff firstTariff = tariffs.get(0);
        assertEquals(1L, firstTariff.getId());
        assertEquals("Тариф 1", firstTariff.getType());
        assertEquals("0.05", firstTariff.getInterestRate());

        Tariff secondTariff = tariffs.get(1);
        assertEquals(2L, secondTariff.getId());
        assertEquals("Тариф 2", secondTariff.getType());
        assertEquals("0.1", secondTariff.getInterestRate());

        Tariff thirdTariff = tariffs.get(2);
        assertEquals(3L, thirdTariff.getId());
        assertEquals("Тариф 3", thirdTariff.getType());
        assertEquals("0.15", thirdTariff.getInterestRate());
    }

    @Test
    void getTariffs_shouldReturnInternalError_whenDataBaseReturnError() throws Exception {
        //mock
        when(tariffRepository.findAll()).thenThrow(new RuntimeException("Test exception"));
        //when
        MvcResult mvcResult = mockMvc.perform(get(GET_TARRIFS_URL_BASE)
                .headers(requestHeaders)
        ).andExpect(status().isInternalServerError()).andReturn();

        //then
        ErrorDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        assertEquals(500, response.getCode());
        assertEquals("Test exception", response.getMessage());
    }
}
