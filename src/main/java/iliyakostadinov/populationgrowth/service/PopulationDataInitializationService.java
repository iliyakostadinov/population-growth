package iliyakostadinov.populationgrowth.service;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;
import iliyakostadinov.populationgrowth.repository.PopulationRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PopulationDataInitializationService {
  public static final int FIRST_AVAILABLE_YEAR = 1960;
  public static final int LAST_AVAILABLE_YEAR = 2021;

  private static final int NUM_HEADER_INFO_LINES = 4;

  @Value("${config.max-prediction-year}")
  private int maxPredictionYear;

  private final PopulationRepository populationRepository;

  public PopulationDataInitializationService(PopulationRepository populationRepository) {
    this.populationRepository = populationRepository;
  }

  @PostConstruct
  private void populateData() throws IOException {
    // file retrieved from https://data.worldbank.org/indicator/SP.POP.TOTL
    File csv = ResourceUtils.getFile("classpath:static/API_SP.POP.TOTL_DS2_en_csv_v2_4218816.csv");

    try (BufferedReader input = new BufferedReader(new FileReader(csv))) {
      // skip the source info lines, so just the relevant part is parsed
      for (int i = 0; i < NUM_HEADER_INFO_LINES; i++) {
        input.readLine();
      }

      try (CSVParser records = CSVFormat.DEFAULT.builder()
                                                .setHeader()
                                                .setIgnoreHeaderCase(true)
                                                .setTrailingDelimiter(true)
                                                .setTrim(true)
                                                .build()
                                                .parse(input)) {

        for (CSVRecord record : records) {
          List<CountryPopulationRecord> countryPopulationRecords =
              new ArrayList<>(LAST_AVAILABLE_YEAR - FIRST_AVAILABLE_YEAR + 1);

          String countryName = record.get("Country Name");
          CountryPopulationRecord countryPopulationRecord;

          for (int year = LAST_AVAILABLE_YEAR; year >= FIRST_AVAILABLE_YEAR; year--) {
            String populationAsString = record.get(Integer.toString(year));

            try {
              long populationForYear = Long.parseLong(populationAsString);
              countryPopulationRecord = new CountryPopulationRecord(countryName,
                                                                    year,
                                                                    populationForYear);
            } catch (NumberFormatException e) {
              System.out.printf("Invalid data for %s in %d: \"%s\"%n",
                                countryName, year, populationAsString);
              countryPopulationRecord = new CountryPopulationRecord(countryName, year, null);
            }

            countryPopulationRecords.add(countryPopulationRecord);
          }

          populationRepository.saveAll(countryPopulationRecords);
          populationRepository.saveAll(
              PopulationPredictor.generatePredictions(countryPopulationRecords, maxPredictionYear));
        }
      }
    }
  }
}
