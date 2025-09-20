package com.example.zomg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HelloMod implements ModInitializer {
    public static final String MODID="zomg";

    public static Item COIN;
    public static Item HEALTH_PACK;
    public static Item STAMINA_DRINK;
    public static Item ARMOR_VEST;
    public static Item AMMO_CRATE;

    public static Block VENDING;

    public static Identifier id(String p){ return new Identifier(MODID, p); }

    @Override public void onInitialize() {
        // items
        COIN = Registry.register(Registry.ITEM, id("coin"), new Item(new Item.Settings().group(ItemGroup.MISC)));
        HEALTH_PACK = Registry.register(Registry.ITEM, id("health_pack"), new HealthPackItem(new Item.Settings().group(ItemGroup.MISC)));
        STAMINA_DRINK = Registry.register(Registry.ITEM, id("stamina_drink"), new StaminaDrinkItem(new Item.Settings().group(ItemGroup.MISC)));
        ARMOR_VEST = Registry.register(Registry.ITEM, id("armor_vest"), new ArmorVestItem(new Item.Settings().group(ItemGroup.MISC)));
        AMMO_CRATE = Registry.register(Registry.ITEM, id("ammo_crate"), new AmmoCrateItem(new Item.Settings().group(ItemGroup.MISC)));

        // vending block
        VENDING = Registry.register(Registry.BLOCK, id("vending"), new VendingBlock(FabricBlockSettings.of(Material.METAL).strength(3.0f)));
        Registry.register(Registry.ITEM, id("vending"), new BlockItem(VENDING, new Item.Settings().group(ItemGroup.DECORATIONS)));

        // systems
        ZomgCampaign.init();
        ZomgWaves.init();
        ZomgDaySpawns.init();
        ZomgDrops.init();
        ZomgShop.init();
    }
}
