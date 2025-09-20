package com.example.zomg;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VendingBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public VendingBlock(Settings s){ super(s); this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH)); }
    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> b){ b.add(FACING); }
    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, net.minecraft.util.math.BlockPos pos, ShapeContext context){ return VoxelShapes.cuboid(0.1,0,0.1,0.9,1.0,0.9); }

    @Override public ActionResult onUse(BlockState state, World world, net.minecraft.util.math.BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (world.isClient) return ActionResult.SUCCESS;
        showShop((net.minecraft.server.network.ServerPlayerEntity)player);
        return ActionResult.SUCCESS;
    }

    private void showShop(net.minecraft.server.network.ServerPlayerEntity p){
        p.sendMessage(Text.literal("§b[자판기] 코인으로 즉시 아이템을 구매합니다."), false);
        offer(p, "응급팩(풀회복)", 50, new ItemStack(HelloMod.HEALTH_PACK));
        offer(p, "스태미나(이속 30초)", 35, new ItemStack(HelloMod.STAMINA_DRINK));
        offer(p, "방탄조끼(1분)", 80, new ItemStack(HelloMod.ARMOR_VEST));
        offer(p, "보급상자(탄약)", 100, new ItemStack(HelloMod.AMMO_CRATE));
    }

    private void offer(net.minecraft.server.network.ServerPlayerEntity p, String name, int cost, ItemStack item){
        int coins = countCoins(p);
        MutableText line = Text.literal(" - " + name + " : " + cost + "코인  ");
        if (coins >= cost){
            line.append(Text.literal("§a[구매]").styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/zbuy " + name))));
        } else { line.append(Text.literal("§c[잔액 부족]")); }
        p.sendMessage(line, false);
    }

    private int countCoins(net.minecraft.server.network.ServerPlayerEntity p){
        int c=0;
        for (int i=0;i<p.getInventory().size();i++){
            var st = p.getInventory().getStack(i);
            if (st!=null && st.getItem()==HelloMod.COIN) c += st.getCount();
        }
        return c;
    }
}
