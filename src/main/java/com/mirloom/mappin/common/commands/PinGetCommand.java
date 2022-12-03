package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

class PinGetCommand {
    static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("get")
                .then(Commands.argument("id", IntegerArgumentType.integer()).executes(
                        ctx -> pinGet(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"))))
                .then(Commands.argument("name", StringArgumentType.greedyString()).suggests(PinCommand.NAMES_SUGGESTION_PROVIDER).executes(
                        ctx -> pinGet(ctx.getSource(), StringArgumentType.getString(ctx, "name"))));
    }

    private static int pinGet(CommandSourceStack sourceStack, int id) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).getPin(id));
    }

    private static int pinGet(CommandSourceStack sourceStack, String name) {
        return run(sourceStack, PinCommand.getPlayerPins(sourceStack).getPin(name));
    }

    private static int run(CommandSourceStack sourceStack, Pin pin) {
        if (pin != null) {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.get.success", pin.id, PinCommand.nameComponent(pin), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.not_found"));
        }
        return 0;
    }
}
