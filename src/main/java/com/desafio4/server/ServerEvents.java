package com.desafio4.server;

import com.desafio4.Desafio4;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Desafio4.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onStarting(ServerStartingEvent e) {
        QuestManager.load();
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent e) {
        D4Commands.register(e.getDispatcher());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            QuestManager.sync(sp);
            QuestManager.visit(sp, sp.level().dimension().location().toString());
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) QuestManager.sync(sp);
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            QuestManager.visit(sp, e.getTo().location().toString());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        if (!(e.getEntity() instanceof ServerPlayer)) return;
        CompoundTag old = e.getOriginal().getPersistentData();
        if (old.contains(QuestManager.KEY)) {
            e.getEntity().getPersistentData().put(QuestManager.KEY, old.getCompound(QuestManager.KEY).copy());
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player instanceof ServerPlayer sp && sp.tickCount % 20 == 0) {
            QuestManager.scanCollect(sp);
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent e) {
        if (e.getSource().getEntity() instanceof ServerPlayer sp) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(e.getEntity().getType());
            if (key != null) QuestManager.progress(sp, "kill", key.toString(), 1);
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent e) {
        if (e.getPlayer() instanceof ServerPlayer sp) {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(e.getState().getBlock());
            if (key != null) QuestManager.progress(sp, "mine", key.toString(), 1);
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(e.getCrafting().getItem());
            if (key != null) QuestManager.progress(sp, "craft", key.toString(), e.getCrafting().getCount());
        }
    }
}
