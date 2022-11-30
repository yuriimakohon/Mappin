package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.Mappin;
import com.mirloom.mappin.common.pins.Pin;
import com.mirloom.mappin.common.pins.PlayerPinsProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Mappin.MOD_ID)
public class PinCommand {
    public PinCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pin")
                .then(Commands.literal("add")
                        .then(Commands.literal("position")
                                .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinAdd(
                                        context.getSource(),
                                        context.getSource().getPlayer().blockPosition(),
                                        StringArgumentType.getString(context, "name"))
                                )))
                        .then(Commands.argument("coordinates", BlockPosArgument.blockPos()).then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinAdd(
                                context.getSource(),
                                BlockPosArgument.getSpawnablePos(context, "coordinates"),
                                StringArgumentType.getString(context, "name"))
                        )))
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("id", IntegerArgumentType.integer()).executes(context -> pinGet(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "id")
                        )))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinGet(
                                context.getSource(),
                                StringArgumentType.getString(context, "name")
                        ))))
                .then(Commands.literal("list")
                        .executes(context -> pinList(context.getSource(), 1)))
                .then(Commands.literal("update")
                        .then(Commands.literal("position").
                                then(Commands.argument("id", IntegerArgumentType.integer()).executes(context -> pinUpdate(
                                        context.getSource(),
                                        context.getSource().getPlayer().blockPosition(),
                                        IntegerArgumentType.getInteger(context, "id")
                                )))
                                .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinUpdate(
                                        context.getSource(),
                                        context.getSource().getPlayer().blockPosition(),
                                        StringArgumentType.getString(context, "name")
                                ))))
                        .then(Commands.argument("coordinates", BlockPosArgument.blockPos())
                                .then(Commands.argument("id", IntegerArgumentType.integer()).executes(context -> pinUpdate(
                                        context.getSource(),
                                        BlockPosArgument.getSpawnablePos(context, "coordinates"),
                                        IntegerArgumentType.getInteger(context, "id")
                                )))
                                .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinUpdate(
                                        context.getSource(),
                                        BlockPosArgument.getSpawnablePos(context, "coordinates"),
                                        StringArgumentType.getString(context, "name")
                                ))))
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("id", IntegerArgumentType.integer()).executes(context -> pinRemove(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "id")
                        )))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).executes(context -> pinRemove(
                                context.getSource(),
                                StringArgumentType.getString(context, "name")
                        )))
                )
        );
    }

    private static int pinAdd(CommandSourceStack sourceStack, BlockPos pos, String name) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.getPin(name);
            if (pin == null) {
                pin = playerPins.addPin(new Pin(playerPins.nextID(), name, pos, sourceStack.getLevel().dimension().location().toString()));
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.add.success", pin.id, pin.formattedName(), pos.getX(), pos.getY(), pos.getZ()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.add.failure.exists", pin.formattedName()));
            }
        });
        return 0;
    }

    private static int pinGet(CommandSourceStack sourceStack, int id) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.getPin(id);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.get.success", id, pin.formattedName(), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    private static int pinGet(CommandSourceStack sourceStack, String name) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.getPin(name);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.get.success", pin.id, pin.formattedName(), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    private static int pinList(CommandSourceStack sourceStack, int page) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            ArrayList<Pin> pins = playerPins.getPins();
            if (pins.size() > 0) {
                MutableComponent listMessage = Component.literal("========| ")
                        .append(Component.translatable("commands.pin.list.header"))
                        .append(Component.literal(" |========\n"));
                pins.forEach(pin -> {
                    listMessage.append(Component.translatable("commands.pin.get.success", pin.id, pin.formattedName(), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
                    if (pin != pins.get(pins.size() - 1)) {
                        listMessage.append(Component.literal("\n"));
                    }
                });
                sourceStack.sendSystemMessage(listMessage);
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.list.empty"));
            }
        });
        return 0;
    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, int id) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.updatePin(id, pos);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.update.success", pin.id, pin.formattedName(), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, String name) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.updatePin(name, pos);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.update.success", pin.id, pin.formattedName(), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    private static int pinRemove(CommandSourceStack sourceStack, int id) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.removePin(id);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.remove.success", pin.formattedName()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    private static int pinRemove(CommandSourceStack sourceStack, String name) {
        ServerPlayer player = sourceStack.getPlayer();
        player.getCapability(PlayerPinsProvider.PLAYER_PINS).ifPresent(playerPins -> {
            Pin pin = playerPins.removePin(name);
            if (pin != null) {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.remove.success", pin.formattedName()));
            } else {
                sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
            }
        });
        return 0;
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new PinCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
