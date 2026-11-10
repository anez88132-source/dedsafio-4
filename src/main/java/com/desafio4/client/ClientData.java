package com.desafio4.client;

import com.desafio4.data.Defs;
import com.google.gson.Gson;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientData {
    private static final Gson GSON = new Gson();
    public static Defs defs = new Defs();
    public static CompoundTag data = new CompoundTag();

    public static void update(String json, CompoundTag tag) {
        try {
            Defs d = GSON.fromJson(json, Defs.class);
            if (d != null) defs = d;
        } catch (Exception ignored) {
        }
        data = tag == null ? new CompoundTag() : tag;
    }

    public static boolean isDone(String id) {
        return data.getCompound("done").getBoolean(id);
    }

    public static int progress(String id) {
        return data.getCompound("progress").getInt(id);
    }

    public static boolean visited(String dim) {
        ListTag l = data.getList("dims", Tag.TAG_STRING);
        for (int i = 0; i < l.size(); i++) if (l.getString(i).equals(dim)) return true;
        return false;
    }

    public static Item item(String id) {
        if (id == null) return Items.AIR;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return Items.AIR;
        Item i = ForgeRegistries.ITEMS.getValue(rl);
        return i == null ? Items.AIR : i;
    }

    /** Nombre legible del objetivo de una misión. */
    public static Component targetName(Defs.Quest q) {
        String t = q.target;
        if (t.startsWith("#")) return Component.literal(t);
        ResourceLocation rl = ResourceLocation.tryParse(t);
        if (rl == null) return Component.literal(t);
        String type = q.type.toLowerCase();
        if (type.equals("collect") || type.equals("craft")) {
            Item i = item(t);
            if (i != Items.AIR) return i.getDescription();
        } else if (type.equals("kill")) {
            if (ForgeRegistries.ENTITY_TYPES.containsKey(rl)) {
                EntityType<?> et = ForgeRegistries.ENTITY_TYPES.getValue(rl);
                if (et != null) return et.getDescription();
            }
        } else if (type.equals("mine")) {
            if (ForgeRegistries.BLOCKS.containsKey(rl)) {
                Block b = ForgeRegistries.BLOCKS.getValue(rl);
                if (b != null) return b.getName();
            }
        } else if (type.equals("dimension")) {
            for (Defs.Dim d : defs.dimensions) if (t.equals(d.id) && !d.name.isEmpty()) return Component.literal(d.name);
        }
        return Component.literal(t);
    }

    /** Lista de objetos importantes: los del JSON + los que piden/dan las misiones. */
    public static Map<String, String> objects() {
        Map<String, String> m = new LinkedHashMap<>();
        for (Defs.Obj o : defs.objects) m.put(o.item, o.note);
        for (Defs.Quest q : defs.quests) {
            String t = q.type.toLowerCase();
            if ((t.equals("collect") || t.equals("craft")) && !q.target.startsWith("#") && !m.containsKey(q.target)) {
                m.put(q.target, Component.translatable("gui.desafio4.needed_for", q.title).getString());
            }
            for (Defs.Reward r : q.rewards) {
                if (r.item != null && !m.containsKey(r.item)) {
                    m.put(r.item, Component.translatable("gui.desafio4.reward_of", q.title).getString());
                }
            }
        }
        return m;
    }
}
