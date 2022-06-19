package iliyakostadinov.populationgrowth.service;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopulationPredictor {
  private PopulationPredictor() {}

  /**
   * This method uses a <i>VERY</i> naive algorithm to predict future population changes.
   *
   * @param populationRecords the available population records for a single country, sorted by year in descending order
   * @return a new list, containing the future population predictions in chronological order,
   * or an empty list if tje provided records do not contain population data.
   */
  public static List<CountryPopulationRecord> generatePredictions(List<CountryPopulationRecord> populationRecords,
                                                                  int lastYearToPredict) {
    CountryPopulationRecord latestRecordWithData =
        populationRecords.stream()
                         .filter((r) -> r.population() != null)
                         .findFirst().orElse(null);

    if (latestRecordWithData == null) {
      return Collections.emptyList();
    }

    int latestYearOfRecord = populationRecords.get(0).year();
    int latestYearWithData = latestRecordWithData.year();

    // find the latest population data available, which is at least 5 years older than the latest
    CountryPopulationRecord referenceRecord =
        populationRecords.stream()
                         .skip(latestYearOfRecord - latestYearWithData + 5)
                         .dropWhile((r) -> r.population() == null)
                         .findFirst().orElse(null);

    long lastAvailablePopulation = latestRecordWithData.population();
    long yearlyDiff;
    if (referenceRecord != null) {
      // we calculate the average change in absolute values and ignore division rounding errors
      yearlyDiff = (lastAvailablePopulation - referenceRecord.population()) / (latestYearWithData - referenceRecord.year());
    } else {
      // if we don't have reference data, assume that the population doesn't change
      yearlyDiff = 0;
    }

    String countryName = latestRecordWithData.country();
    long expectedPopulation = Math.max(0, lastAvailablePopulation + yearlyDiff);

    List<CountryPopulationRecord> predictions =
        new ArrayList<>(lastYearToPredict - latestYearOfRecord);

    for (int year = latestYearOfRecord + 1; year <= lastYearToPredict; year++) {
      predictions.add(new CountryPopulationRecord(countryName, year, expectedPopulation));

      expectedPopulation = Math.max(0, expectedPopulation + yearlyDiff);
    }

    return predictions;
  }
}
