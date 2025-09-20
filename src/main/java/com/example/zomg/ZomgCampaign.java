package com.example.zomg;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.nbt.NbtCompound;

public class ZomgCampaign {
    public static class CampState extends PersistentState {
        public long startTick = -1L;
        public int dayIndex = 1;
        public int phaseTicks = 0;
        public boolean isNight = false;
        @Override public NbtCompound writeNbt(NbtCompound n){
            n.putLong("start", startTick);
            n.putInt("day", dayIndex);
            n.putInt("phase", phaseTicks);
            n.putBoolean("night", isNight);
            return n;
        }
        public static CampState fromNbt(NbtCompound n){
            CampState s=new CampState();
            s.startTick = n.getLong("start");
            s.dayIndex = Math.max(1, n.getInt("day"));
            s.phaseTicks = n.getInt("phase");
            s.isNight = n.getBoolean("night");
            return s;
        }
    }

    public static final int TICKS = 20;
    public static final int DAY_T = 6*60*TICKS;
    public static final int NIGHT_T = 4*60*TICKS;
    private static CampState state;

    public static void init(){
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PersistentStateManager psm = server.getOverworld().getPersistentStateManager();
            state = psm.getOrCreate(CampState::fromNbt, CampState::new, "zomg_campaign");
        });
        ServerTickEvents.END_SERVER_TICK.register(ZomgCampaign::tick);
    }

    public static boolean isNight(){ return state!=null && state.isNight; }
    public static int currentDay(){ return state==null?1:state.dayIndex; }

    private static void tick(MinecraftServer server){
        if (state==null) return;
        if (state.startTick<0){ state.startTick=server.getOverworld().getTime(); state.phaseTicks=0; state.isNight=false; state.dayIndex=1; state.markDirty(); server.sendMessage(Text.literal("[ZOMG] Day 1 시작 (낮)")); }
        state.phaseTicks++;
        if (!state.isNight){
            if (state.phaseTicks >= DAY_T){
                state.isNight = true; state.phaseTicks=0; state.markDirty();
                server.sendMessage(Text.literal("[ZOMG] Night " + state.dayIndex + " 시작!"));
                ZomgWaves.onNightStart(server, state.dayIndex);
            }
        } else {
            if (state.phaseTicks >= NIGHT_T){
                state.isNight = false; state.phaseTicks=0; state.dayIndex++; state.markDirty();
                if (state.dayIndex>12){ ZomgWaves.triggerEnding(server); }
                else { server.sendMessage(Text.literal("[ZOMG] Day " + state.dayIndex + " 시작 (낮)")); }
            }
        }
    }
}
