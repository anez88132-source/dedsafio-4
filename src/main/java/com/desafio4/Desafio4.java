package com.desafio4;

import com.desafio4.net.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Desafio4.MODID)
public class Desafio4 {
    public static final String MODID = "desafio4";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Desafio4() {
        ModNetwork.register();
    }
}
