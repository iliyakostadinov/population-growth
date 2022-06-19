package iliyakostadinov.populationgrowth.service;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PopulationPredictorTest {
  private static final String countryName = "test";
  private static final int lastYearWithData = 2020;

  @Test
  public void producesExpectedResultWithCompleteData() {
    int requestedFutureYears = 50;
    long latestPopulation = 110L;

    List<CountryPopulationRecord> countryPopulationData =
        List.of(new CountryPopulationRecord(countryName, lastYearWithData, latestPopulation),
                new CountryPopulationRecord(countryName, lastYearWithData - 1, 106L),
                new CountryPopulationRecord(countryName, lastYearWithData - 2, 101L),
                new CountryPopulationRecord(countryName, lastYearWithData - 3, 102L),
                new CountryPopulationRecord(countryName, lastYearWithData - 4, 103L),
                new CountryPopulationRecord(countryName, lastYearWithData - 5, 100L));

    List<CountryPopulationRecord> countryPopulationRecords =
        PopulationPredictor.generatePredictions(countryPopulationData,
                                                lastYearWithData + requestedFutureYears);

    assertEquals(countryPopulationRecords.size(), requestedFutureYears);

    for (int i = 1; i <= requestedFutureYears; i++) {
      assertEquals(latestPopulation + 2L * i,
                   countryPopulationRecords.get(i - 1).population()
      );
    }
  }

  @Test
  public void producesExpectedResultWithIncompleteData() {
    int requestedFutureYears = 50;
    long latestPopulation = 124L;

    List<CountryPopulationRecord> countryPopulationData =
        List.of(new CountryPopulationRecord(countryName, lastYearWithData, latestPopulation),
                new CountryPopulationRecord(countryName, lastYearWithData - 1, 106L),
                new CountryPopulationRecord(countryName, lastYearWithData - 2, 101L),
                new CountryPopulationRecord(countryName, lastYearWithData - 3, 102L),
                new CountryPopulationRecord(countryName, lastYearWithData - 4, null),
                new CountryPopulationRecord(countryName, lastYearWithData - 5, null),
                new CountryPopulationRecord(countryName, lastYearWithData - 6, null),
                new CountryPopulationRecord(countryName, lastYearWithData - 7, null),
                new CountryPopulationRecord(countryName, lastYearWithData - 8, 100L));

    List<CountryPopulationRecord> countryPopulationRecords =
        PopulationPredictor.generatePredictions(countryPopulationData,
                                                lastYearWithData + requestedFutureYears);

    assertEquals(countryPopulationRecords.size(), requestedFutureYears);

    for (int i = 1; i <= requestedFutureYears; i++) {
      assertEquals(latestPopulation + 3L * i,
                   countryPopulationRecords.get(i - 1).population()
      );
    }
  }

  @Test
  public void predictsNoChangeWithSingleDataEntry() {
    int requestedFutureYears = 50;
    long latestPopulation = 74896L;

    List<CountryPopulationRecord> countryPopulationData =
        List.of(new CountryPopulationRecord(countryName, lastYearWithData, latestPopulation));

    List<CountryPopulationRecord> countryPopulationRecords =
        PopulationPredictor.generatePredictions(countryPopulationData,
                                                lastYearWithData + requestedFutureYears);

    assertEquals(countryPopulationRecords.size(), requestedFutureYears);

    for (int i = 0; i < requestedFutureYears; i++) {
      assertEquals(latestPopulation, countryPopulationRecords.get(i).population());
    }
  }

  @Test
  public void returnsEmptyListIfNoUsableDataIsProvided() {
    List<CountryPopulationRecord> countryPopulationData =
        List.of(new CountryPopulationRecord(countryName, lastYearWithData, null));

    List<CountryPopulationRecord> countryPopulationRecords =
        PopulationPredictor.generatePredictions(countryPopulationData,
                                                lastYearWithData + 2);

    assertTrue(countryPopulationRecords.isEmpty());
    assertTrue(PopulationPredictor.generatePredictions(List.of(), 2050).isEmpty());
  }
}
