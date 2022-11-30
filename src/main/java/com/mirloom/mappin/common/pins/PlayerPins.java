package com.mirloom.mappin.common.pins;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.ArrayList;

@AutoRegisterCapability
public class PlayerPins {
    private ArrayList<Pin> pins = new ArrayList<>();
    private int nextID = 1;

    public int nextID() {
        return nextID++;
    }

    public Pin addPin(Pin pin) {
        pins.add(pin);
        return pin;
    }

    public Pin getPin(int id) {
        for (Pin pin : pins) {
            if (pin.id == id) {
                return pin;
            }
        }
        return null;
    }

    public Pin getPin(String name) {
        for (Pin pin : pins) {
            if (pin.name.equals(name)) {
                return pin;
            }
        }
        return null;
    }

    public ArrayList<Pin> getPins() {
        return pins;
    }

    public Pin updatePin(int id, BlockPos newPos) {
        for (Pin pin : pins) {
            if (pin.id == id) {
                pin.pos = newPos;
                return pins.set(pins.indexOf(pin), pin);
            }
        }
        return null;
    }

    public Pin updatePin(String name, BlockPos newPos) {
        for (Pin pin : pins) {
            if (pin.name.equals(name)) {
                pin.pos = newPos;
                return pins.set(pins.indexOf(pin), pin);
            }
        }
        return null;
    }

    public Pin removePin(int id) {
        for (Pin pin : pins) {
            if (pin.id == id) {
                return remove(pins.indexOf(pin));
            }
        }
        return null;
    }

    public Pin removePin(String name) {
        for (Pin pin : pins) {
            if (pin.name.equals(name)) {
                return remove(pins.indexOf(pin));
            }
        }
        return null;
    }

    private Pin remove(int index) {
        for (int i = index + 1; i < pins.size(); i++) {
            Pin pin = pins.get(i);
            pin.id--;
            pins.set(i, pin);
        }
        nextID--;
        return pins.remove(index);
    }

    public void copyFrom(PlayerPins playerPins) {
        pins = playerPins.pins;
        nextID = playerPins.nextID;
    }

    public CompoundTag serializeNBT() {
        ListTag listTag = new ListTag();
        if (pins.size() > 0) {
            for (int i = 0; i < pins.size(); i++) {
                Pin pin = pins.get(i);
                pin.id = i + 1;
                listTag.add(pin.toNBT());
            }
            nextID = pins.size() + 1;
        }

        CompoundTag pinsTag = new CompoundTag();
        pinsTag.put("list", listTag);
        pinsTag.putInt("next_id", nextID);

        CompoundTag nbt = new CompoundTag();
        nbt.put("pins", pinsTag);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag pinsTag = nbt.getCompound("pins");
        nextID = pinsTag.getInt("next_id");

        ListTag listTag = pinsTag.getList("list", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            pins.add(Pin.fromNBT(listTag.getCompound(i)));
        }
    }
}
