package iliyakostadinov.populationgrowth.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataInitializationService {
  private static final int NUM_HEADER_INFO_LINES = 4;
  private static final int FIRST_AVAILABLE_YEAR = 1960;
  private static final int LAST_AVAILABLE_YEAR = 2021;

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
          Map<Integer, Long> populationByYear = new HashMap<>();

          String countryName = record.get("Country Name");

          for (int year = FIRST_AVAILABLE_YEAR; year <= LAST_AVAILABLE_YEAR; year++) {
            String stringValue = record.get(Integer.toString(year));
            Long populationForYear = null;

            try {
              populationForYear = Long.valueOf(stringValue);
            } catch (NumberFormatException e) {
              System.out.printf("Invalid data for %s in %d: \"%s\"%n", countryName, year, stringValue);
            }

            populationByYear.put(year, populationForYear);
          }
        }
      }
    }
  }
}
