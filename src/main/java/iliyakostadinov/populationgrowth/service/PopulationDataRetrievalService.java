package iliyakostadinov.populationgrowth.service;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import iliyakostadinov.populationgrowth.repository.PopulationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static iliyakostadinov.populationgrowth.service.PopulationDataInitializationService.FIRST_AVAILABLE_YEAR;

@Service
public class PopulationDataRetrievalService {
  private static final int MIN_RESULTS = 1;
  private static final int MAX_RESULTS = 100;

  @Value("${config.max-prediction-year}")
  private int maxPredictionYear;

  private final PopulationRepository populationRepository;

  public PopulationDataRetrievalService(PopulationRepository populationRepository) {
    this.populationRepository = populationRepository;
  }

  public CountryPopulationRecord getCountryPopulationInYear(String country, int year) {
    validateYearRange(year);

    return populationRepository.getByCountryAndYear(country, year);
  }

  public List<String> getMostPopulatedCountriesInYear(int year, int limit) {
    validateYearRange(year);

    if (limit < MIN_RESULTS || limit > MAX_RESULTS) {
      throw new IllegalArgumentException(
          String.format("Requested result size must be in range [%d, %d]", MIN_RESULTS, MAX_RESULTS));
    }

    List<CountryPopulationRecord> mostPopulatedCountries =
        populationRepository.getNMostPopulatedCountriesInYear(limit, year);

    if (mostPopulatedCountries == null) {
      return null;
    }

    return mostPopulatedCountries.stream()
                                 .filter(r -> r.population() != null)
                                 .map(CountryPopulationRecord::country)
                                 .collect(Collectors.toList());
  }

  private void validateYearRange(int year) {
    if (year < FIRST_AVAILABLE_YEAR || year > maxPredictionYear) {
      throw new IllegalArgumentException(String.format("Year must be in range [%d, %d]",
                                                       FIRST_AVAILABLE_YEAR, maxPredictionYear));
    }
  }
}
