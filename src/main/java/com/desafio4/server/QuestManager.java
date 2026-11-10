package com.desafio4.server;

import com.desafio4.Desafio4;
import com.desafio4.data.DefaultConfig;
import com.desafio4.data.Defs;
import com.desafio4.net.ModNetwork;
import com.desafio4.net.SyncPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    public static final String KEY = "desafio4";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Defs defs = new Defs();
    public static String json = "{}";

    // ---------- Carga de configuración ----------
    public static void load() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("desafio4");
        Path file = dir.resolve("desafio4.json");
        try {
            Files.createDirectories(dir);
            if (!Files.exists(file)) Files.writeString(file, DefaultConfig.JSON);
            Defs d = GSON.fromJson(Files.readString(file), Defs.class);
            if (d != null) {
                d.quests.removeIf(q -> q == null || q.id == null || q.type == null || q.target == null);
                d.objects.removeIf(o -> o == null || o.item == null);
                d.dimensions.removeIf(x -> x == null || x.id == null);
                defs = d;
            }
        } catch (Exception e) {
            Desafio4.LOGGER.error("No se pudo leer desafio4.json", e);
        }
        json = GSON.toJson(defs);
        Desafio4.LOGGER.info("Desafío 4: {} misiones cargadas", defs.quests.size());
    }

    // ---------- Datos del jugador (NBT persistente) ----------
    static CompoundTag sub(CompoundTag parent, String k) {
        if (!parent.contains(k, Tag.TAG_COMPOUND)) parent.put(k, new CompoundTag());
        return parent.getCompound(k);
    }

    public static CompoundTag root(Player p) {
        return sub(p.getPersistentData(), KEY);
    }

    public static void sync(ServerPlayer p) {
        ModNetwork.send(p, new SyncPacket(json, root(p).copy()));
    }

    // ---------- Progreso ----------
    static boolean matches(String questTarget, String actual) {
        return questTarget.equals("*") || questTarget.equals(actual);
    }

    public static void progress(ServerPlayer p, String type, String target, int amount) {
        CompoundTag r = root(p);
        CompoundTag prog = sub(r, "progress");
        CompoundTag done = sub(r, "done");
        boolean changed = false;
        for (Defs.Quest q : defs.quests) {
            if (!q.type.equalsIgnoreCase(type) || done.getBoolean(q.id)) continue;
            if (!matches(q.target, target)) continue;
            int max = Math.max(1, q.amount);
            int cur = prog.getInt(q.id);
            int nw = Math.min(max, cur + amount);
            if (nw != cur) {
                prog.putInt(q.id, nw);
                changed = true;
            }
            if (nw >= max) {
                complete(p, q);
                changed = true;
            }
        }
        if (changed) sync(p);
    }

    static boolean itemMatches(ItemStack s, String target) {
        if (target.startsWith("#")) {
            ResourceLocation rl = ResourceLocation.tryParse(target.substring(1));
            return rl != null && s.is(TagKey.create(Registries.ITEM, rl));
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(s.getItem());
        return key != null && key.toString().equals(target);
    }

    /** Misiones tipo "collect": cuenta lo que lleva el jugador en el inventario. */
    public static void scanCollect(ServerPlayer p) {
        CompoundTag r = root(p);
        CompoundTag prog = sub(r, "progress");
        CompoundTag done = sub(r, "done");
        boolean changed = false;
        List<ItemStack> all = new ArrayList<>();
        all.addAll(p.getInventory().items);
        all.addAll(p.getInventory().armor);
        all.addAll(p.getInventory().offhand);
        for (Defs.Quest q : defs.quests) {
            if (!q.type.equalsIgnoreCase("collect") || done.getBoolean(q.id)) continue;
            int count = 0;
            for (ItemStack s : all) if (!s.isEmpty() && itemMatches(s, q.target)) count += s.getCount();
            int max = Math.max(1, q.amount);
            int nw = Math.min(max, count);
            if (nw != prog.getInt(q.id)) {
                prog.putInt(q.id, nw);
                changed = true;
            }
            if (nw >= max) {
                complete(p, q);
                changed = true;
            }
        }
        if (changed) sync(p);
    }

    public static void visit(ServerPlayer p, String dim) {
        CompoundTag r = root(p);
        ListTag l = r.getList("dims", Tag.TAG_STRING);
        boolean has = false;
        for (int i = 0; i < l.size(); i++) if (l.getString(i).equals(dim)) has = true;
        if (!has) {
            l.add(StringTag.valueOf(dim));
            r.put("dims", l);
        }
        progress(p, "dimension", dim, 1);
        if (!has) sync(p);
    }

    static void complete(ServerPlayer p, Defs.Quest q) {
        CompoundTag r = root(p);
        sub(r, "done").putBoolean(q.id, true);
        sub(r, "progress").putInt(q.id, Math.max(1, q.amount));
        p.sendSystemMessage(Component.literal("§6[" + defs.title + "] §a¡Misión completada: §e" + q.title));
        p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1f);
        for (Defs.Reward rw : q.rewards) {
            ResourceLocation rl = ResourceLocation.tryParse(rw.item == null ? "" : rw.item);
            if (rl == null) continue;
            Item it = ForgeRegistries.ITEMS.getValue(rl);
            if (it == null || it == Items.AIR) continue;
            ItemStack st = new ItemStack(it, Math.max(1, rw.count));
            if (!p.getInventory().add(st)) p.drop(st, false);
        }
    }

    public static boolean forceComplete(ServerPlayer p, String id) {
        for (Defs.Quest q : defs.quests) {
            if (q.id.equals(id)) {
                if (!root(p).getCompound("done").getBoolean(id)) complete(p, q);
                sync(p);
                return true;
            }
        }
        return false;
    }

    public static void reset(ServerPlayer p) {
        p.getPersistentData().remove(KEY);
        visit(p, p.level().dimension().location().toString());
        sync(p);
    }
}
