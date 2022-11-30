package com.mirloom.mappin.common.pins;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class Pin {
    public int id;
    public String name;
    public BlockPos pos;

    public Pin(int id, String name, BlockPos pos) {
        this.id = id;
        this.name = name;
        this.pos = pos;
    }

    public static Pin fromNBT(CompoundTag tag) {
        return new Pin(tag.getInt("id"), tag.getString("name"), BlockPos.of(tag.getLong("pos")));
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("name", name);
        tag.putLong("pos", pos.asLong());
        return tag;
    }
}
