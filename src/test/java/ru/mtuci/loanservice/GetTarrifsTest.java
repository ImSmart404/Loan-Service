package ru.mtuci.loanservice;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import ru.mtuci.loanservice.DTO.ErrorDTO;
import ru.mtuci.loanservice.DTO.GetTariffsDataResponseDTO;
import ru.mtuci.loanservice.model.Tariff;
import ru.mtuci.loanservice.repository.TariffRepository;

import java.util.ArrayList;
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

        List<Tariff> tariffList = createTestTariffs();

        when(tariffRepository.findAll()).thenReturn(tariffList);

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

    private List<Tariff> createTestTariffs() {
        List<Tariff> tariffs = new ArrayList<>();

        Tariff firstTariff = new Tariff(1L, "Тариф 1", "0.05");
        Tariff secondTariff = new Tariff(2L, "Тариф 2", "0.1");
        Tariff thirdTariff = new Tariff(3L, "Тариф 3", "0.15");

        tariffs.add(firstTariff);
        tariffs.add(secondTariff);
        tariffs.add(thirdTariff);
        return tariffs;
    }
}
