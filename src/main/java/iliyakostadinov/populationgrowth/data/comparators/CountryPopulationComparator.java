package iliyakostadinov.populationgrowth.data.comparators;

import iliyakostadinov.populationgrowth.data.CountryPopulationRecord;

import java.util.Comparator;
import java.util.Objects;

public final class CountryPopulationComparator {

  public static Comparator<CountryPopulationRecord> POPULATION_DESC = Comparator.comparing(
      CountryPopulationRecord::population, (pop1, pop2) -> {
        if (Objects.equals(pop1, pop2)) {
          return 0;
        }

        // treat null as the lowest population
        if (pop1 == null) {
          return 1;
        }

        if (pop2 == null) {
          return -1;
        }

        return pop1 > pop2 ? -1 : 1;
      }).thenComparing(CountryPopulationRecord::country, String::compareToIgnoreCase);

  private CountryPopulationComparator() {}
}
