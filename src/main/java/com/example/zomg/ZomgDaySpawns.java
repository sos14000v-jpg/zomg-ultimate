package com.example.zomg;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import java.util.Random;

public class ZomgDaySpawns {
    private static final Random R = new Random();
    private static int ticker = 0;

    public static void init(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ZomgCampaign.isNight()) return;
            ticker++;
            if (ticker % (20*30) != 0) return;
            ServerWorld w = server.getOverworld();
            for (ServerPlayerEntity p : w.getPlayers()){
                int nearby = 0;
                for (var e : w.iterateEntities()){
                    if (e instanceof ZombieEntity z && z.getPersistentData().getBoolean("DAYZ")){
                        if (z.squaredDistanceTo(p) < 32*32) nearby++;
                    }
                }
                if (nearby >= 3) continue;
                int need = Math.min(3-nearby, 1 + R.nextInt(3));
                for (int i=0;i<need;i++){
                    int r = 16 + R.nextInt(16);
                    double ang = R.nextDouble()*Math.PI*2;
                    int x = (int)(p.getX() + Math.cos(ang)*r);
                    int z = (int)(p.getZ() + Math.sin(ang)*r);
                    int y = w.getTopY()-1;
                    BlockPos bp = new BlockPos(x,y,z);
                    while(y>w.getBottomY()+2 && w.isAir(bp)){ y--; bp = new BlockPos(x,y,z); }
                    BlockPos spawn = bp.up();
                    ZombieEntity zmb = new ZombieEntity(w);
                    zmb.refreshPositionAndAngles(spawn, 0, 0);
                    if (R.nextDouble()<0.10){
                        var sp = zmb.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                        if (sp!=null) sp.setBaseValue(sp.getBaseValue()*1.3);
                        zmb.getPersistentData().putString("ZTYPE","SPRINTER");
                    } else { zmb.getPersistentData().putString("ZTYPE","NORMAL"); }
                    zmb.getPersistentData().putBoolean("DAYZ", true);
                    w.spawnEntity(zmb);
                }
            }
        });
    }
}
