package com.example.zomg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HealthPackItem extends Item {
    public HealthPackItem(Settings s){ super(s); }
    @Override public ActionResult use(World world, PlayerEntity user, Hand hand){
        if (!world.isClient){
            user.heal(user.getMaxHealth());
            user.getItemCooldownManager().set(this, 20*5);
            ItemStack st = user.getStackInHand(hand); st.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
