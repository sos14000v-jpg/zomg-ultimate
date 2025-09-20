package com.example.zomg;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;

public class ZomgDrops {
    public static void init(){
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
            if (!(killed instanceof ZombieEntity)) return;
            String type = ((ZombieEntity)killed).getPersistentData().getString("ZTYPE");
            int coins = 0;
            if ("SPRINTER".equals(type)) coins = 3;
            else if ("ELITE".equals(type)) coins = 4;
            else if ("BOSS".equals(type)) coins = 20;
            else { if (world.getRandom().nextBoolean()) coins = 1; }
            if (coins>0) killed.dropStack(new ItemStack(HelloMod.COIN, coins));
            if ("BOSS".equals(type)) killed.dropStack(new ItemStack(HelloMod.COIN, 10));
        });
    }
}
