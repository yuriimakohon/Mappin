package com.mirloom.mappin;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Mappin.MOD_ID)
public class Mappin {
    public static final String MOD_ID = "mappin";

    public Mappin() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.register(this);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
