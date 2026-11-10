package com.desafio4.client;

import com.desafio4.Desafio4;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Desafio4.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping OPEN = new KeyMapping("key.desafio4.open", InputConstants.KEY_J, "key.categories.desafio4");

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent e) {
        e.register(OPEN);
    }
}
