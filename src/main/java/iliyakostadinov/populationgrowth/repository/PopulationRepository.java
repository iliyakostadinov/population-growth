package iliyakostadinov.populationgrowth.repository;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import iliyakostadinov.populationgrowth.data.comparators.CountryPopulationComparator;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class PopulationRepository {
  private final Map<String, Map<Integer, CountryPopulationRecord>> countriesPopulationByYear = new HashMap<>();
  private final Map<Integer, Set<CountryPopulationRecord>> sortedPopulationsByYear = new HashMap<>();

  public void saveAll(List<CountryPopulationRecord> populationData) {
    if (populationData == null) {
      throw new NullPointerException("Population data cannot be null.");
    }

    for (CountryPopulationRecord populationRecord : populationData) {
      int year = populationRecord.year();

      countriesPopulationByYear
          .computeIfAbsent(populationRecord.country().toLowerCase(), (k) -> new HashMap<>())
          .put(year, populationRecord);

      sortedPopulationsByYear
          .computeIfAbsent(year, (k) -> new TreeSet<>(CountryPopulationComparator.POPULATION_DESC))
          .add(populationRecord);
    }
  }

  public CountryPopulationRecord getByCountryAndYear(String countryName, int year) {
    if (countryName == null) {
      throw new NullPointerException("Country name cannot be null.");
    }

    return Optional.ofNullable(countriesPopulationByYear.get(countryName.toLowerCase()))
                   .map(m -> m.get(year))
                   .orElse(null);
  }

  public List<CountryPopulationRecord> getNMostPopulatedCountriesInYear(int n, int year) {
    if (n < 0) {
      throw new IllegalArgumentException("Result set size cannot be negative.");
    }

    Set<CountryPopulationRecord> countryPopulationsForYearRecord = sortedPopulationsByYear.get(year);

    if (countryPopulationsForYearRecord == null) {
      return null;
    }

    return countryPopulationsForYearRecord.stream()
                                          .limit(n)
                                          .collect(Collectors.toList());
  }

  void clear() {
    countriesPopulationByYear.clear();
    sortedPopulationsByYear.clear();
  }
}
