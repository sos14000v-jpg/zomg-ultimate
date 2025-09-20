package com.example.zomg;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ZomgShop {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(CommandManager.literal("zbuy").then(
                CommandManager.argument("name", net.minecraft.command.argument.StringArgumentType.string())
                    .executes(ctx -> {
                        String name = net.minecraft.command.argument.StringArgumentType.getString(ctx, "name");
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        return buy(p, name);
                    })
            ));
        });
    }

    private static int buy(ServerPlayerEntity p, String name){
        int cost = 0; ItemStack item = ItemStack.EMPTY;
        if (name.contains("응급")){ cost=50; item = new ItemStack(HelloMod.HEALTH_PACK); }
        else if (name.contains("스태미나")){ cost=35; item = new ItemStack(HelloMod.STAMINA_DRINK); }
        else if (name.contains("방탄")){ cost=80; item = new ItemStack(HelloMod.ARMOR_VEST); }
        else if (name.contains("보급")){ cost=100; item = new ItemStack(HelloMod.AMMO_CRATE); }
        if (cost==0){ p.sendMessage(Text.literal("잘못된 상품"), false); return 0; }
        if (!removeCoins(p, cost)){ p.sendMessage(Text.literal("코인이 부족합니다."), false); return 0; }
        p.giveItemStack(item);
        p.sendMessage(Text.literal("구매 완료: " + name), false);
        return 1;
    }

    private static boolean removeCoins(ServerPlayerEntity p, int need){
        for (int i=0;i<p.getInventory().size();i++){
            var st = p.getInventory().getStack(i);
            if (st!=null && st.getItem()==HelloMod.COIN){
                int take = Math.min(need, st.getCount());
                st.decrement(take);
                need -= take;
                if (need<=0) return true;
            }
        }
        return false;
    }
}
