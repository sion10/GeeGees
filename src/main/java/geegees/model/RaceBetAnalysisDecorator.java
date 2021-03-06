package geegees.model;

import com.google.common.base.Predicate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

public class RaceBetAnalysisDecorator {
    private Race race;

    public RaceBetAnalysisDecorator(Race race) {
        this.race = race;
        for (Horse horse : race.getHorses()) {
            horse.setDecimalOdds(getDecimalOdds(horse));
        }
        setBettable();
        if (race.getBettable()) {
            final List<Horse> horses = newArrayList(race.getHorses());
            Collections.sort(horses, new Comparator<Horse>() {
                @Override
                public int compare(Horse h1, Horse h2) {
                    return h1.getDecimalOdds() > h2.getDecimalOdds() ? 1 :
                            h1.getDecimalOdds() < h2.getDecimalOdds() ? -1 :
                                    0;
                }
            });
            if (horses.get(0).getOdds().equals(horses.get(1).getOdds())) {
                //more than one favourite
                for (Horse horse : filter(horses, new Predicate<Horse>() {
                    @Override
                    public boolean apply(Horse horse) {
                        return horses.get(0).getOdds().equals(horse.getOdds());
                    }
                })) {
                    horse.setMagicNumber(0 + horse.getTips() - race.getNumberOfRunners());
                }
            } else {
                horses.get(0).setMagicNumber(horses.get(1).getDecimalOdds() - horses.get(0).getDecimalOdds()
                        + horses.get(0).getTips() - race.getNumberOfRunners());
            }
        }
    }

    private void setBettable() {
        for (Horse horse : race.getHorses()) {
            if (horse.getDecimalOdds() >= 1d && horse.getDecimalOdds() <= 2d) {
                race.setBettable(true);
            }
        }
    }

    private double getDecimalOdds(Horse horse) {
        if (horse.getOdds().equals("Evs")) {
            return 1d;
        }

        int slashAt = horse.getOdds().indexOf("/");
        Double numerator = Double.valueOf(horse.getOdds().substring(0, slashAt));
        Double denominator = Double.valueOf(horse.getOdds().substring(slashAt + 1));
        return numerator / denominator;
    }

    public Race getRace() {
        return race;
    }
}
