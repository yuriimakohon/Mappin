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

import java.util.Objects;

public class PinUpdateCommand {
    static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("update")
                .then(Commands.literal("position")
                        .then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                                ctx -> pinUpdate(ctx.getSource(), Objects.requireNonNull(ctx.getSource().getPlayer()).blockPosition(), IntegerArgumentType.getInteger(ctx, "id"))))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.namesSuggestionProvider).executes(
                                ctx -> pinUpdate(ctx.getSource(), Objects.requireNonNull(ctx.getSource().getPlayer()).blockPosition(), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.argument("coordinates", BlockPosArgument.blockPos())
                        .then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                                ctx -> pinUpdate(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), IntegerArgumentType.getInteger(ctx, "id"))))
                        .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.namesSuggestionProvider).executes(
                                ctx -> pinUpdate(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "coordinates"), StringArgumentType.getString(ctx, "name")))))
                .then(Commands.literal("name")
                        .then(Commands.argument("id", IntegerArgumentType.integer()).then(Commands.argument("new name", StringArgumentType.greedyString()).executes(
                                ctx -> pinRename(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), StringArgumentType.getString(ctx, "new name")))))
                        .then(Commands.argument("old name", StringArgumentType.string()).suggests(PinCommand.quotedNamesSuggestionProvider).then(Commands.argument("new name", StringArgumentType.greedyString()).executes(
                                ctx -> pinRename(ctx.getSource(), StringArgumentType.getString(ctx, "old name"), StringArgumentType.getString(ctx, "new name"))))));
    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, int id) {
        return runUpdate(sourceStack, PinCommand.getPlayerPins(sourceStack).updatePin(id, pos));
    }

    private static int pinUpdate(CommandSourceStack sourceStack, BlockPos pos, String name) {
        return runUpdate(sourceStack, PinCommand.getPlayerPins(sourceStack).updatePin(name, pos));
    }

    private static int runUpdate(CommandSourceStack sourceStack, Pin pin) {
        if (pin != null) {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.update.success", pin.id, PinCommand.nameComponent(pin), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
        }
        return 0;
    }

    private static int pinRename(CommandSourceStack sourceStack, String oldName, String newName) {
        return runRename(sourceStack, PinCommand.getPlayerPins(sourceStack).renamePin(oldName, newName));
    }

    private static int pinRename(CommandSourceStack sourceStack, int id, String newName) {
        return runRename(sourceStack, PinCommand.getPlayerPins(sourceStack).renamePin(id, newName));
    }

    private static int runRename(CommandSourceStack sourceStack, Pin pin) {
        if (pin != null) {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.update.name.success", pin.id, PinCommand.nameComponent(pin)));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
        }
        return 0;
    }
}
