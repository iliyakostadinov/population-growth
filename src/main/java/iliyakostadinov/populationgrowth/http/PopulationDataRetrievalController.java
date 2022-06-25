package iliyakostadinov.populationgrowth.http;

import iliyakostadinov.populationgrowth.http.response.PopulationResponse;
import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import iliyakostadinov.populationgrowth.service.PopulationDataRetrievalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/population")
public class PopulationDataRetrievalController {
  private final PopulationDataRetrievalService dataService;

  public PopulationDataRetrievalController(PopulationDataRetrievalService dataService) {
    this.dataService = dataService;
  }

  @GetMapping(value = "/countries/{country}/{year:\\d+}")
  public PopulationResponse<CountryPopulationRecord> getCountryPopulationInYear(@PathVariable("country") String country,
                                                                                @PathVariable("year") Integer year) {
    CountryPopulationRecord result;
    try {
      result = dataService.getCountryPopulationInYear(country, year);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    if (result == null) {
      return new PopulationResponse<>(null, HttpStatus.NOT_FOUND);
    }

    if (result.population() == null) {
      return new PopulationResponse<>(null, HttpStatus.NO_CONTENT);
    }

    return new PopulationResponse<>(result, HttpStatus.OK);
  }

  @GetMapping(value = "/year/{year:\\d+}")
  public PopulationResponse<List<String>> getMostPopulatedCountriesInYear(@PathVariable("year") Integer year) {
    List<String> result;
    try {
      result = dataService.getMostPopulatedCountriesInYear(year, 20);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    if (result == null || result.isEmpty()) {
      return new PopulationResponse<>(null, HttpStatus.NO_CONTENT);
    }

    return new PopulationResponse<>(result, HttpStatus.OK);
  }
}
