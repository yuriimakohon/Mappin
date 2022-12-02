package com.mirloom.mappin.common.commands;

import com.mirloom.mappin.common.pins.Pin;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;

class PinListCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("list").executes(ctx -> run(ctx.getSource()));
    }

    private static int run(CommandSourceStack sourceStack) {
        ArrayList<Pin> pins = PinCommand.getPlayerPins(sourceStack).getPins();
        if (pins.size() > 0) {
            MutableComponent listMessage = Component.literal("========| ")
                    .append(Component.translatable("commands.pin.list.header"))
                    .append(Component.literal(" |========\n"));
            pins.forEach(pin -> {
                listMessage.append(Component.translatable("commands.pin.get.success", pin.id, PinCommand.nameComponent(pin), pin.pos.getX(), pin.pos.getY(), pin.pos.getZ()));
                if (pin != pins.get(pins.size() - 1)) {
                    listMessage.append(Component.literal("\n"));
                }
            });
            sourceStack.sendSystemMessage(listMessage);
        } else {
            sourceStack.sendSystemMessage(Component.translatable("commands.pin.list.empty"));
        }
        return 0;
    }
}
