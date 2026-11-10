package com.desafio4.net;

import com.desafio4.Desafio4;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Desafio4.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        CH.registerMessage(0, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
    }

    public static void send(ServerPlayer p, SyncPacket pkt) {
        CH.send(PacketDistributor.PLAYER.with(() -> p), pkt);
    }
}
