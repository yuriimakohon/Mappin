package com.mirloom.mappin.common.pins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Pin {
    public int id;
    public String name;
    public BlockPos pos;
    public String dimension;

    public Pin(int id, String name, BlockPos pos, String dimension) {
        this.id = id;
        this.name = name;
        this.pos = pos;
        this.dimension = dimension;
    }

    public MutableComponent formattedName() {
        MutableComponent component = Component.literal(name);
        switch (dimension) {
            case "minecraft:overworld":
                component.withStyle(ChatFormatting.GREEN);
                break;
            case "minecraft:the_nether":
                component.withStyle(ChatFormatting.DARK_RED);
                break;
            case "minecraft:the_end":
                component.withStyle(ChatFormatting.DARK_PURPLE);
                break;
            default:
                component.withStyle(ChatFormatting.GRAY);
        }
        return component;
    }

    public static Pin fromNBT(CompoundTag tag) {
        return new Pin(
                tag.getInt("id"),
                tag.getString("name"),
                BlockPos.of(tag.getLong("pos")),
                tag.getString("dim"));
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("name", name);
        tag.putLong("pos", pos.asLong());
        tag.putString("dim", dimension);
        return tag;
    }
}
