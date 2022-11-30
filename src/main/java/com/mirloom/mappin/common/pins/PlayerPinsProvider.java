package com.mirloom.mappin.common.pins;

import com.mirloom.mappin.Mappin;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Mappin.MOD_ID)
public class PlayerPinsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerPins> PLAYER_PINS = CapabilityManager.get(new CapabilityToken<PlayerPins>() {
    });

    private PlayerPins pins = null;
    private final LazyOptional<PlayerPins> optional = LazyOptional.of(this::createPlayerPins);

    private PlayerPins createPlayerPins() {
        if (this.pins == null) {
            this.pins = new PlayerPins();
        }

        return this.pins;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_PINS.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return createPlayerPins().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerPins().deserializeNBT(nbt);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PLAYER_PINS).isPresent()) {
                event.addCapability(new ResourceLocation(Mappin.MOD_ID, "properties"), new PlayerPinsProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PLAYER_PINS).ifPresent(oldStore -> {
                event.getEntity().getCapability(PLAYER_PINS).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }
}
