package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class PinRemove {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("remove")
                .then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                        ctx -> pinRemove(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"))))
                .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.namesSuggestionProvider).executes(
                        ctx -> pinRemove(ctx.getSource(), StringArgumentType.getString(ctx, "name"))));
    }

    private static int pinRemove(CommandSourceStack sourceStack, int id) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).removePin(id));
    }

    private static int pinRemove(CommandSourceStack sourceStack, String name) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).removePin(name));
    }

    private static int run(CommandSourceStack sourceStack, Pin pin) {
        if (pin != null) {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.remove.success", PinCommand.nameComponent(pin)));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
        }
        return 0;
    }

}
