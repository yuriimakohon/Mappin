package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.Mappin;
import com.mirloom.mappin.common.pins.Pin;
import com.mirloom.mappin.common.pins.PlayerPins;
import com.mirloom.mappin.common.pins.PlayerPinsProvider;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Mappin.MOD_ID)
public class PinCommand {
    static SuggestionProvider<CommandSourceStack> namesSuggestionProvider = (context, builder) -> {
        getPlayerPins(context.getSource()).getPins().forEach(pin -> builder.suggest(pin.name));
        return builder.buildFuture();
    };

    static SuggestionProvider<CommandSourceStack> quotedNamesSuggestionProvider = (context, builder) -> {
        getPlayerPins(context.getSource()).getPins().forEach(pin -> builder.suggest('"' + pin.name + '"'));
        return builder.buildFuture();
    };

    static PlayerPins getPlayerPins(CommandSourceStack sourceStack) {
        ServerPlayer player = sourceStack.getPlayer();
        assert player != null;
        return player.getCapability(PlayerPinsProvider.PLAYER_PINS).orElseThrow(() -> new IllegalStateException("PlayerPins capability not found"));
    }

    static MutableComponent nameComponent(Pin pin) {
        MutableComponent component = Component.literal(pin.name);
        switch (pin.dimension) {
            case "minecraft:overworld" -> component.withStyle(ChatFormatting.GREEN);
            case "minecraft:the_nether" -> component.withStyle(ChatFormatting.DARK_RED);
            case "minecraft:the_end" -> component.withStyle(ChatFormatting.DARK_PURPLE);
            default -> component.withStyle(ChatFormatting.GRAY);
        }

        String command = String.format("/execute in %s run tp %d %d %d", pin.dimension, pin.pos.getX(), pin.pos.getY(), pin.pos.getZ());
        component.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
        return component;
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pin")
                .then(PinAddCommand.build())
                .then(PinGetCommand.build())
                .then(PinListCommand.build())
                .then(PinUpdateCommand.build())
                .then(PinRemove.build())
        );
        ConfigCommand.register(event.getDispatcher());
    }
}



