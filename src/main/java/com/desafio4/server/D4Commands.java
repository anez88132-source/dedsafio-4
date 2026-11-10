package com.desafio4.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class D4Commands {
    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("desafio4")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("reload").executes(c -> {
                    QuestManager.load();
                    for (ServerPlayer p : c.getSource().getServer().getPlayerList().getPlayers()) QuestManager.sync(p);
                    c.getSource().sendSuccess(() -> Component.literal("Desafío 4 recargado (" + QuestManager.defs.quests.size() + " misiones)"), true);
                    return 1;
                }))
                .then(Commands.literal("reset").then(Commands.argument("player", EntityArgument.player()).executes(c -> {
                    ServerPlayer p = EntityArgument.getPlayer(c, "player");
                    QuestManager.reset(p);
                    c.getSource().sendSuccess(() -> Component.literal("Progreso reiniciado: " + p.getName().getString()), true);
                    return 1;
                })))
                .then(Commands.literal("complete").then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("quest", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                                        QuestManager.defs.quests.stream().map(q -> q.id), b))
                                .executes(c -> {
                                    ServerPlayer p = EntityArgument.getPlayer(c, "player");
                                    String id = StringArgumentType.getString(c, "quest");
                                    if (QuestManager.forceComplete(p, id)) {
                                        c.getSource().sendSuccess(() -> Component.literal("Misión completada: " + id), true);
                                        return 1;
                                    }
                                    c.getSource().sendFailure(Component.literal("No existe la misión: " + id));
                                    return 0;
                                }))))
        );
    }
}
