package com.desafio4.net;

import com.desafio4.client.ClientData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPacket {
    public final String json;
    public final CompoundTag data;

    public SyncPacket(String json, CompoundTag data) {
        this.json = json;
        this.data = data;
    }

    public static void encode(SyncPacket m, FriendlyByteBuf b) {
        b.writeUtf(m.json, 262144);
        b.writeNbt(m.data);
    }

    public static SyncPacket decode(FriendlyByteBuf b) {
        return new SyncPacket(b.readUtf(262144), b.readNbt());
    }

    public static void handle(SyncPacket m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientData.update(m.json, m.data)));
        ctx.get().setPacketHandled(true);
    }
}
