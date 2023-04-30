import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mtc.loanservice.LoanserviceApplication;
import ru.mtc.loanservice.model.Tariff;
import ru.mtc.loanservice.repository.TariffRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = LoanserviceApplication.class)

public class TariffControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TariffRepository tariffRepository;

    @Test
    public void testGetTariffs() throws Exception {
        List<Tariff> tariffs = Arrays.asList(
                new Tariff(1L, "Tariff 1", "10.0"),
                new Tariff(2L, "Tariff 2", "20.0")
        );

        Mockito.when(tariffRepository.findAll()).thenReturn(tariffs);

        mockMvc.perform(get("/getTariffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tariffs[0].id").value(1))
                .andExpect(jsonPath("$.data.tariffs[0].name").value("Tariff 1"))
                .andExpect(jsonPath("$.data.tariffs[0].price").value(10.0))
                .andExpect(jsonPath("$.data.tariffs[1].id").value(2))
                .andExpect(jsonPath("$.data.tariffs[1].name").value("Tariff 2"))
                .andExpect(jsonPath("$.data.tariffs[1].price").value(20.0));
    }
}
