package uk.kihira.immersivefloofs;

import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.tool.BulletHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import uk.kihira.tails.common.Tails;

@Mod(modid = ImmersiveFloofs.MOD_ID, name = "Immersive Floofs", version = "1.0.0", dependencies = "required-after:immersiveengineering;required-after:tails@[1.9,)")
public class ImmersiveFloofs {
    public static final String MOD_ID = "immersivefloofs";

    /** CONFIG **/
    private Configuration config;
    private boolean randomBullet = false;
    private boolean shooterBullet = true;
    //private boolean craftedBullet = true;
    private boolean milkResets = true;

    @GameRegistry.ObjectHolder(value = "immersiveengineering:bullet")
    public static final Item IE_ITEM_BULLET = Items.APPLE;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        /* Load config */
        config = new Configuration(e.getSuggestedConfigurationFile());
        shooterBullet = config.getBoolean("Shooter Floof Bullet", Configuration.CATEGORY_GENERAL, true, "This bullet will apply the shooters parts data to the target");
        //craftedBullet = config.getBoolean("Crafted Floof Bullet", Configuration.CATEGORY_GENERAL, true, "This bullet will store the crafters parts data on the bullet");
        randomBullet = config.getBoolean("Random Floof Bullet", Configuration.CATEGORY_GENERAL, false, "This bullet will apply random parts from a list");
        milkResets = config.getBoolean("Milk Resets", Configuration.CATEGORY_GENERAL, true, "Whether drinking milk resets the 'effect'");

        /* Register bullets **/
        if (shooterBullet) BulletHandler.registerBullet("floof_shooter", new FloofBullet("floof_shooter") {
            @Override
            public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
                if (Tails.proxy.hasPartsData(shooter.getPersistentID())) {
                    projectile.getEntityData().setString("immersivefloofs", Tails.gson.toJson(Tails.proxy.getPartsData(shooter.getPersistentID())));
                }
                return projectile;
            }
        });
//        if (craftedBullet) BulletHandler.registerBullet("floof_crafted", new FloofBullet("floof_crafted") {
//            @Override
//            public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
//                if (cartridge.hasTagCompound() && cartridge.getTagCompound().hasKey("immersivefloofs")) {
//                    projectile.getEntityData().setString("immersivefloofs", cartridge.getTagCompound().getString("immersivefloofs"));
//                }
//                return projectile;
//            }
//        });
        if (randomBullet) BulletHandler.registerBullet("floof_random", new FloofBullet("floof_random")); // todo

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent e) {
        if (IE_ITEM_BULLET == null || IE_ITEM_BULLET == Items.APPLE) throw new IllegalStateException("Unable to load IE bullet item!");
        /* Register recipes */
        // todo proper recipes
        ItemStack output; // Metadata must be 2 or above but doesn't seem to really matter
        if (shooterBullet) {
            output = new ItemStack(IE_ITEM_BULLET, 1, 2);
            output.setTagCompound(new NBTTagCompound() {{setString("bullet", "floof_shooter");}});
            BlueprintCraftingRecipe.addRecipe("specialBullet", output, IE_ITEM_BULLET, Items.GUNPOWDER, Blocks.WOOL);
        }
//        if (craftedBullet) {
//            output = new ItemStack(IE_ITEM_BULLET, 1, 2);
//            output.setTagCompound(new NBTTagCompound() {{setString("bullet", "floof_crafted");}});
//            BlueprintCraftingRecipe.addRecipe("specialBullet", output, IE_ITEM_BULLET, Items.LEATHER, Items.LEATHER);
//        }
        if (randomBullet) {
            output = new ItemStack(IE_ITEM_BULLET, 1, 2);
            output.setTagCompound(new NBTTagCompound() {{setString("bullet", "floof_random");}});
            BlueprintCraftingRecipe.addRecipe("specialBullet", output, IE_ITEM_BULLET, Items.GUNPOWDER, Items.LEATHER);
        }
    }

    @SubscribeEvent
    public void onMilkDrink(LivingEntityUseItemEvent.Finish e) {
        if (milkResets && e.getItem().getItem() instanceof ItemBucketMilk) {
            // todo reset
        }
    }
}
