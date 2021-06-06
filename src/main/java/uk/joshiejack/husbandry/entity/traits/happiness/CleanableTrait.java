package uk.joshiejack.husbandry.entity.traits.happiness;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import uk.joshiejack.husbandry.api.IMobStats;
import uk.joshiejack.husbandry.api.trait.AbstractMobTrait;
import uk.joshiejack.husbandry.api.trait.IDataTrait;
import uk.joshiejack.husbandry.api.trait.IInteractiveTrait;
import uk.joshiejack.husbandry.api.trait.INewDayTrait;
import uk.joshiejack.husbandry.network.SetCleanedStatusPacket;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helpers.generic.MathsHelper;

public class CleanableTrait extends AbstractMobTrait implements IDataTrait, IInteractiveTrait, INewDayTrait {
    private int cleanliness;
    private boolean cleaned;

    public CleanableTrait(String name) {
        super(name);
    }

    @Override
    public void onNewDay(IMobStats<?> stats) {
        setCleaned(stats, false);
        cleanliness = MathsHelper.constrainToRangeInt(cleanliness - 10, -100, 100);
        if (cleanliness <= 0) {
            stats.decreaseHappiness(1); //We dirty, so we no happy
        }
    }

    @Override
    public boolean onRightClick(IMobStats<?> stats, PlayerEntity player, Hand hand) {
        return false;
    }


    public boolean isClean() {
        return cleaned;
    }

    public boolean clean(IMobStats<?> stats) {
        if (!cleaned) {
            cleanliness++;
            if (cleanliness == 100) {
                setCleaned(stats, true);
            }
        }

        return cleaned;
    }

    public void setCleaned(IMobStats<?> stats, boolean cleaned) {
        MobEntity entity = stats.getEntity();
        if (entity.level.isClientSide) this.cleaned = cleaned;
        else {
            this.cleaned = cleaned;
            if (cleaned) {
                stats.increaseHappiness(30);
            }

            PenguinNetwork.sendToNearby(new SetCleanedStatusPacket(entity.getId(), cleaned), entity);
        }
    }

    @Override
    public void load(CompoundNBT nbt) {
        cleanliness = nbt.getInt("Cleanliness");
        cleaned = nbt.getBoolean("Cleaned");
    }

    @Override
    public void save(CompoundNBT tag) {
        tag.putInt("Cleanliness", cleanliness);
        tag.putBoolean("Cleaned", cleaned);
    }
}