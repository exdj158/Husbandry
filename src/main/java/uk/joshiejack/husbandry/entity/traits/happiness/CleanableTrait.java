package uk.joshiejack.husbandry.entity.traits.happiness;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import uk.joshiejack.husbandry.Husbandry;
import uk.joshiejack.husbandry.api.IMobStats;
import uk.joshiejack.husbandry.api.trait.IDataTrait;
import uk.joshiejack.husbandry.api.trait.IInteractiveTrait;
import uk.joshiejack.husbandry.api.trait.INewDayTrait;
import uk.joshiejack.husbandry.item.HusbandryItems;
import uk.joshiejack.husbandry.network.SetCleanedStatusPacket;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helpers.generic.MathsHelper;

public class CleanableTrait implements IDataTrait, IInteractiveTrait, INewDayTrait {
    private int cleanliness;
    private boolean cleaned;

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
        if (player.getItemInHand(hand).getItem() == HusbandryItems.BRUSH.get() && clean(stats)) {
            World world = player.level;
            MobEntity target = stats.getEntity();
            if (world.isClientSide) {
                for (int j = 0; j < 30D; j++) {
                    double d7 = (target.xo - 0.5D) + world.random.nextFloat();
                    double d8 = (target.yo - 0.5D) + world.random.nextFloat();
                    double d9 = (target.zo - 0.5D) + world.random.nextFloat();
                    world.addParticle(ParticleTypes.HAPPY_VILLAGER, d8, 1.0D + d7 - 0.125D, d9, 0, 0, 0);
                }
            }

            world.playSound(player, player.xo, player.yo, player.zo, Husbandry.HusbandrySounds.BRUSH.get(), SoundCategory.PLAYERS, 1.5F, 1F);
            return true;
        }

        return false;
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