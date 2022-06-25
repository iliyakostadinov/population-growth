package iliyakostadinov.populationgrowth.http;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import iliyakostadinov.populationgrowth.service.PopulationDataRetrievalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PopulationDataRetrievalControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PopulationDataRetrievalService service;

  @Test
  public void getForExistingCountryAndYearReturnsCorrectResponse() throws Exception {
    CountryPopulationRecord expectedResult = new CountryPopulationRecord("Bulgaria",
                                                                         2020,
                                                                         8_000_000L);

    when(service.getCountryPopulationInYear(expectedResult.country(), expectedResult.year()))
        .thenReturn(expectedResult);

    this.mockMvc.perform(
            get("/population/countries/" + expectedResult.country() + "/" + expectedResult.year()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.country").value(expectedResult.country()))
                .andExpect(jsonPath("$.result.year").value(expectedResult.year()))
                .andExpect(jsonPath("$.result.population").value(expectedResult.population()));
  }

  @Test
  public void getForNonExistingCountryAndYearReturnsNotFound() throws Exception {
    this.mockMvc.perform(get("/population/countries/test/2020"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
  }

  @Test
  public void getForCountryAndYearWithoutPopulationDataReturnsNoData() throws Exception {
    CountryPopulationRecord expectedResult = new CountryPopulationRecord("Bulgaria", 2021, null);

    when(service.getCountryPopulationInYear(expectedResult.country(), expectedResult.year()))
        .thenReturn(expectedResult);

    this.mockMvc.perform(
            get("/population/countries/" + expectedResult.country() + "/" + expectedResult.year()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
  }


  @Test
  public void getForYearTooFarInTheFutureReturnsBadRequest() throws Exception {
    String errorMessage = "error message";

    when(service.getCountryPopulationInYear(any(), intThat(i -> i > 3000)))
        .thenThrow(new IllegalArgumentException(errorMessage));

    when(service.getMostPopulatedCountriesInYear(intThat(i -> i > 3000), anyInt()))
        .thenThrow(new IllegalArgumentException(errorMessage));

    this.mockMvc.perform(get("/population/countries/bulgaria/5000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));

    this.mockMvc.perform(get("/population/year/5000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
  }


  @Test
  public void getForMostPopulatedCountriesReturnsCorrectResponse() throws Exception {
    int year = 2020;
    String[] countries = {"China", "India", "USA", "Indonesia", "Brazil"};

    when(service.getMostPopulatedCountriesInYear(eq(year), anyInt()))
        .thenReturn(Arrays.asList(countries));

    ResultActions resultActions = this.mockMvc.perform(get("/population/year/" + year))
                                              .andExpect(status().isOk())
                                              .andExpect(jsonPath("$.result").isArray());

    for (int i = 0; i < countries.length; i++) {
      resultActions.andExpect(jsonPath("$.result[" + i + "]").value(countries[i]));
    }
  }

  @Test
  public void getForYearWithoutPopulationDataReturnsNoData() throws Exception {
    when(service.getMostPopulatedCountriesInYear(anyInt(), anyInt()))
        .thenReturn(null, List.of());

    // call with null
    this.mockMvc.perform(get("/population/year/2020"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

    // call with empty list
    this.mockMvc.perform(get("/population/year/2020"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
  }
}
