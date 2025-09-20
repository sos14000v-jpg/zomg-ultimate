package com.example.zomg;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ArmorVestItem extends Item {
    public ArmorVestItem(Settings s){ super(s); }
    @Override public ActionResult use(World world, PlayerEntity user, Hand hand){
        if (!world.isClient){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20*60, 0));
            ItemStack st = user.getStackInHand(hand); st.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
