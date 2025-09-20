package com.example.zomg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AmmoCrateItem extends Item {
    public AmmoCrateItem(Settings s){ super(s); }
    @Override public ActionResult use(World world, PlayerEntity user, Hand hand){
        if (!world.isClient){
            user.giveItemStack(new ItemStack(HelloMod.COIN, 1)); // placeholder reward
            ItemStack st = user.getStackInHand(hand); st.decrement(1);
        }
        return ActionResult.SUCCESS;
    }
}
