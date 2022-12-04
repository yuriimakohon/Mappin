package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mirloom.mappin.common.pins.PlayerPins;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Objects;

class PinAddCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("add")
                .then(Commands.literal("position").then(Commands.argument("name", StringArgumentType.greedyString()).executes(
                        ctx -> pinAdd(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.argument("coordinates", BlockPosArgument.blockPos()).then(Commands.literal("dimension").then(Commands.argument("dimension", ResourceLocationArgument.id()).suggests(PinCommand.DIMENSION_SUGGESTION_PROVIDER).then(Commands.argument("name", StringArgumentType.greedyString()).executes(
                        ctx -> pinAdd(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), ResourceLocationArgument.getId(ctx, "dimension").toString(), StringArgumentType.getString(ctx, "name")))))))
                .then(Commands.argument("coordinates", BlockPosArgument.blockPos()).then(Commands.argument("name", StringArgumentType.greedyString()).executes(
                        ctx -> pinAdd(ctx.getSource(), StringArgumentType.getString(ctx, "name")))));
    }

    private static int pinAdd(CommandSourceStack sourceStack, String name) {
        return run(sourceStack, new Pin(name, Objects.requireNonNull(sourceStack.getPlayer()).blockPosition(), sourceStack.getLevel().dimension().location().toString()));
    }

    private static int pinAdd(CommandSourceStack sourceStack, BlockPos pos, String dimension, String name) {
        return run(sourceStack, new Pin(name, pos, dimension));
    }

    private static int run(CommandSourceStack sourceStack, Pin pin) {
        PlayerPins playerPins = PinCommand.getPlayerPins(sourceStack);
        if (playerPins.getPin(pin.name) == null) {
            pin.id = playerPins.nextID();
            pin = playerPins.addPin(pin);
            sourceStack.sendSystemMessage(net.minecraft.network.chat.Component.translatable("commands.pin.add.success", pin.id, PinCommand.nameComponent(pin), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.add.failure.exists", PinCommand.nameComponent(pin)));
        }
        return 0;
    }
}
