package com.example.zomg;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.*;

public class ZomgWaves {
    private static class WaveProg { int spawned=0; int target=0; int tick=0; }
    private static final Map<UUID, WaveProg> active = new HashMap<>();
    private static final Random R = new Random();

    public static void init(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ZomgCampaign.isNight()) return;
            int day = ZomgCampaign.currentDay();
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()){
                WaveProg w = active.get(p.getUuid());
                if (w==null) continue;
                w.tick++;
                if (w.spawned >= w.target) continue;
                if (w.tick % 20 != 0) continue;
                spawnNear(server.getOverworld(), p, day, w);
            }
        });
    }

    public static void onNightStart(MinecraftServer server, int day){
        int baseMin, baseMax;
        if (day<=3){ baseMin=25; baseMax=25; }
        else if (day==4){ baseMin=90; baseMax=120; }
        else if (day<=7){ baseMin=70; baseMax=90; }
        else if (day==8){ baseMin=130; baseMax=160; }
        else if (day<=11){ baseMin=90; baseMax=110; }
        else { baseMin=200; baseMax=250; }
        int nPlayers = Math.min(4, server.getPlayerManager().getPlayerList().size());
        float scale = 1f; if (nPlayers==2) scale=0.7f; else if (nPlayers==3) scale=0.6f; else if (nPlayers>=4) scale=0.5f;

        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()){
            int target = (int)Math.round((baseMin + R.nextInt(baseMax - baseMin + 1)) * scale);
            WaveProg w = new WaveProg(); w.target=target;
            active.put(p.getUuid(), w);
            p.sendMessage(Text.literal("[ZOMG] 오늘 밤 웨이브: " + target + "마리 (개인 기준)"), false);
        }
    }

    public static void triggerEnding(MinecraftServer server){
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()){
            p.sendMessage(Text.literal("[ZOMG] 12일 클리어! GG"), false);
        }
        active.clear();
    }

    private static void spawnNear(ServerWorld world, ServerPlayerEntity p, int day, WaveProg w){
        Vec3d pos = p.getPos();
        int r = 24 + R.nextInt(10);
        double ang = R.nextDouble()*Math.PI*2;
        int x = (int)(pos.x + Math.cos(ang)*r);
        int z = (int)(pos.z + Math.sin(ang)*r);
        int y = world.getTopY()-1;
        BlockPos bp = new BlockPos(x,y,z);
        while(y>world.getBottomY()+2 && world.isAir(bp)){ y--; bp = new BlockPos(x,y,z); }
        BlockPos spawn = bp.up();

        double eliteRate=0, sprRate=0; boolean boss=false;
        if (day<=3){ sprRate=0.05; }
        else if (day==4){ sprRate=0.20; eliteRate=0.10; }
        else if (day<=7){ sprRate=0.25; eliteRate=0.15; }
        else if (day==8){ sprRate=0.30; eliteRate=0.20; boss=true; }
        else if (day<=11){ sprRate=0.25; eliteRate=0.20; }
        else { sprRate=0.25; eliteRate=0.15; boss=true; }

        if (boss && w.spawned==0){ spawnZombie(world, spawn, "BOSS"); w.spawned++; return; }

        String type="NORMAL";
        double r0 = R.nextDouble();
        if (r0 < eliteRate) type="ELITE";
        else if (r0 < eliteRate + sprRate) type="SPRINTER";
        spawnZombie(world, spawn, type);
        w.spawned++;
        if (day==12 && w.spawned == Math.max(1, w.target/2)){ spawnZombie(world, spawn, "BOSS"); w.spawned++; }
    }

    private static void spawnZombie(ServerWorld world, BlockPos pos, String type){
        ZombieEntity z = new ZombieEntity(world);
        z.refreshPositionAndAngles(pos, 0, 0);
        z.getPersistentData().putString("ZTYPE", type);
        if ("SPRINTER".equals(type)){
            var sp = z.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (sp!=null) sp.setBaseValue(sp.getBaseValue()*1.3);
        } else if ("ELITE".equals(type)){
            var hp = z.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (hp!=null){ hp.setBaseValue(hp.getBaseValue()*2.0); z.setHealth((float)hp.getBaseValue()); }
            var kb = z.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            if (kb!=null) kb.setBaseValue(Math.min(1.0, kb.getBaseValue()+0.3));
            var dmg = z.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (dmg!=null) dmg.setBaseValue(dmg.getBaseValue()*1.5);
        } else if ("BOSS".equals(type)){
            var hp = z.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (hp!=null){ hp.setBaseValue(hp.getBaseValue()*8.0); z.setHealth((float)hp.getBaseValue()); }
            var kb = z.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            if (kb!=null) kb.setBaseValue(Math.min(1.0, kb.getBaseValue()+0.8));
            var dmg = z.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (dmg!=null) dmg.setBaseValue(dmg.getBaseValue()*3.0);
        }
        world.spawnEntity(z);
    }
}
