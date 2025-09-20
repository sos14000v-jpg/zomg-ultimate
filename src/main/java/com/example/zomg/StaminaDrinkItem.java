package com.example.zomg;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class StaminaDrinkItem extends Item {
    public StaminaDrinkItem(Settings s){ super(s); }
    @Override public ActionResult use(World world, PlayerEntity user, Hand hand){
        if (!world.isClient){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20*30, 1));
            ItemStack st = user.getStackInHand(hand); st.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
