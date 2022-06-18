package iliyakostadinov.populationgrowth.repository;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PopulationRepositoryTest {
  private static final int NUM_YEARS_WITH_DATA = 4;
  private static final Object[][] testData = {
      {"A", 2020, 123L}, {"A", 2021, 125L}, {"A", 2022, 128L}, {"A", 2023, 128L},
      {"B", 2020, 127L}, {"B", 2021, 126L}, {"B", 2022, 125L}, {"B", 2023, null},
      {"C", 2020, 130L}, {"C", 2021, 125L}, {"C", 2022, 120L}, {"C", 2023, 0L}
  };
  private static final PopulationRepository repository = new PopulationRepository();

  @Test
  public void queryingExistingRecordReturnsIt() {
    assertEquals(new CountryPopulationRecord("A", 2021, 125L),
                 repository.getByCountryAndYear("A", 2021));
  }

  @Test
  public void queryingByCountryNameIsNotCaseSensitive() {
    assertEquals(new CountryPopulationRecord("A", 2021, 125L),
                 repository.getByCountryAndYear("a", 2021));
  }

  @Test
  public void queryingNonExistingRecordReturnsNull() {
    assertNull(repository.getByCountryAndYear("", 0));
  }

  @Test
  public void queryingWithNullCountryThrowsException() {
    Exception ex = assertThrows(NullPointerException.class,
                                () -> repository.getByCountryAndYear(null, 2020));

    assertTrue(ex.getMessage().contains("cannot be null"));
  }

  @Test
  public void queryingMostPopulatedCountriesReturnsCorrectlySortedData() {
    List<CountryPopulationRecord> actual = repository.getNMostPopulatedCountriesInYear(3, 2021);

    List<CountryPopulationRecord> expected = new ArrayList<>();
    expected.add(mapTestDataToRecord(testData[5]));
    expected.add(mapTestDataToRecord(testData[1]));
    expected.add(mapTestDataToRecord(testData[9]));

    assertEquals(expected, actual); // records with same population are ordered lexicographically

    actual = repository.getNMostPopulatedCountriesInYear(3, 2023);

    expected.clear();
    expected.add(mapTestDataToRecord(testData[3]));
    expected.add(mapTestDataToRecord(testData[11]));
    expected.add(mapTestDataToRecord(testData[7]));

    assertEquals(expected, actual); // no population is treated as less than 0 population
  }

  @Test
  public void queryingMostPopulatedCountriesWithoutDataReturnsNull() {
    assertNull(repository.getNMostPopulatedCountriesInYear(2, 2030));
  }

  @Test
  public void queryingMostPopulatedCountriesReturnsCorrectAmountOfEntries() {
    int expectedSize = 1;

    assertEquals(expectedSize,
                 repository.getNMostPopulatedCountriesInYear(expectedSize, 2020).size());
  }

  @Test
  public void queryingMostPopulatedCountriesWithMoreThanAvailableRecordsReturnsAllAvailableRecords() {
    int expectedSize = testData.length / NUM_YEARS_WITH_DATA;

    assertEquals(expectedSize,
                 repository.getNMostPopulatedCountriesInYear(expectedSize * 2, 2020).size());
  }

  @Test
  public void queryingMostPopulatedCountriesWithNegativeAmountThrowsException() {
    Exception ex = assertThrows(IllegalArgumentException.class,
                                () -> repository.getNMostPopulatedCountriesInYear(-1, 2030));

    assertTrue(ex.getMessage().contains("cannot be negative"));
  }

  @Test
  public void savingNullThrowsException() {
    Exception ex = assertThrows(NullPointerException.class, () -> repository.saveAll(null));

    assertTrue(ex.getMessage().contains("cannot be null"));
  }

  @BeforeAll
  private static void prepareData() {
    List<CountryPopulationRecord> records =
        Arrays.stream(testData)
              .map(PopulationRepositoryTest::mapTestDataToRecord)
              .collect(Collectors.toList());

    repository.saveAll(records);
  }

  @AfterAll
  private static void resetRepositoryState() {
    repository.clear();
  }

  private static CountryPopulationRecord mapTestDataToRecord(Object[] entry) {
    return new CountryPopulationRecord((String) entry[0], (Integer) entry[1], (Long) entry[2]);
  }
}
