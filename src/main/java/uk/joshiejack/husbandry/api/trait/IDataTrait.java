package uk.joshiejack.husbandry.api.trait;

import net.minecraft.nbt.CompoundNBT;

public interface IDataTrait extends IMobTrait {
    void load(CompoundNBT nbt);

    void save(CompoundNBT nbt);
}
