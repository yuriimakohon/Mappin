package com.mirloom.mappin.common.pins;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.ArrayList;

@AutoRegisterCapability
public class PlayerPins {
    private ArrayList<Pin> pins = new ArrayList<>();
    private int nextID = 1;
    private int deathCount = 1;

    private int nextID() {
        return nextID++;
    }

    private int nextDeathCount() {
        return deathCount++;
    }

    public Pin addPin(Pin pin) {
        pin.id = nextID();
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

    public Pin renamePin(int id, String newName) {
        for (Pin pin : pins) {
            if (pin.id == id) {
                pin.name = newName;
                return pins.set(pins.indexOf(pin), pin);
            }
        }
        return null;
    }

    public Pin renamePin(String oldName, String newName) {
        for (Pin pin : pins) {
            if (pin.name.equals(oldName)) {
                pin.name = newName;
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

    public Pin addDeathPin(BlockPos deathPos, String dimension) {
        return addPin(new Pin(Component.translatable("mappin.death_pin_name") + " " + nextDeathCount(), deathPos, dimension));
    }

    public void copyFrom(PlayerPins playerPins) {
        pins = playerPins.pins;
        nextID = playerPins.nextID;
        deathCount = playerPins.deathCount;
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
        pinsTag.putInt("death_count", deathCount);

        CompoundTag nbt = new CompoundTag();
        nbt.put("pins", pinsTag);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag pinsTag = nbt.getCompound("pins");
        nextID = pinsTag.getInt("next_id");
        deathCount = pinsTag.getInt("death_count");

        ListTag listTag = pinsTag.getList("list", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            pins.add(Pin.fromNBT(listTag.getCompound(i)));
        }
    }
}
