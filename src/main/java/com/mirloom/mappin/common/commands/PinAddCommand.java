package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mirloom.mappin.common.pins.PlayerPins;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

class PinAddCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("add").then(Commands.literal("position").then(Commands.argument("name", StringArgumentType.greedyString()).executes(
                        ctx -> pinAdd(ctx.getSource(), ctx.getSource().getPlayer().blockPosition(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.argument("coordinates", BlockPosArgument.blockPos()).then(Commands.argument("name", StringArgumentType.greedyString()).executes(
                        ctx -> pinAdd(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), StringArgumentType.getString(ctx, "name")))));
    }

    private static int pinAdd(CommandSourceStack sourceStack, BlockPos pos, String name) {
        PlayerPins playerPins = PinCommand.getPlayerPins(sourceStack);
        Pin pin = playerPins.getPin(name);
        if (pin == null) {
            pin = playerPins.addPin(new Pin(playerPins.nextID(), name, pos, sourceStack.getLevel().dimension().location().toString()));
            sourceStack.sendSystemMessage(net.minecraft.network.chat.Component.translatable("commands.pin.add.success", pin.id, PinCommand.nameComponent(pin), pos.getX(), pos.getY(), pos.getZ()));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.add.failure.exists", PinCommand.nameComponent(pin)));
        }
        return 0;
    }
}
