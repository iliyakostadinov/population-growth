package iliyakostadinov.populationgrowth.data;

import java.util.Objects;

public record CountryPopulationRecord(String country, int year, Long population) {
  public CountryPopulationRecord {
    Objects.requireNonNull(country);

    if (country.isBlank()) {
      throw new IllegalArgumentException("Country value cannot be empty.");
    }

    if (population != null && population < 0) {
      throw new IllegalArgumentException("Population cannot be negative.");
    }
  }
}
