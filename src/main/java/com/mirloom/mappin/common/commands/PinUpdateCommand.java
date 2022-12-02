package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class PinUpdateCommand {
    static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("update")
                .then(Commands.literal("position").then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                                ctx -> pinUpdate(ctx.getSource(), ctx.getSource().getPlayer().blockPosition(), IntegerArgumentType.getInteger(ctx, "id"))))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.pinNamesSuggestionProvider).executes(
                                ctx -> pinUpdate(ctx.getSource(), ctx.getSource().getPlayer().blockPosition(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.argument("coordinates", BlockPosArgument.blockPos())
                        .then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                                ctx -> pinUpdate(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), IntegerArgumentType.getInteger(ctx, "id"))))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.pinNamesSuggestionProvider).executes(
                                ctx -> pinUpdate(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), StringArgumentType.getString(ctx, "name")))));
    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, int id) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).updatePin(id, pos));

    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, String name) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).updatePin(name, pos));
    }

    private static int run(CommandSourceStack sourceStack, Pin pin) {
        if (pin != null) {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.update.success", pin.id, PinCommand.nameComponent(pin), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
        }
        return 0;
    }
}
