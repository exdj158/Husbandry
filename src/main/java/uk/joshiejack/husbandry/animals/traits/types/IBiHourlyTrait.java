package uk.joshiejack.husbandry.animals.traits.types;

import uk.joshiejack.husbandry.animals.stats.AnimalStats;

public interface IBiHourlyTrait extends IAnimalTrait {
    void onBihourlyTick(AnimalStats<?> stats);
}
