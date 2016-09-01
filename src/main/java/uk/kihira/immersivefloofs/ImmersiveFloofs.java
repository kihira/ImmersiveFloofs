package uk.kihira.immersivefloofs;

import blusunrize.immersiveengineering.api.tool.BulletHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.kihira.tails.common.Tails;

@Mod(modid = ImmersiveFloofs.MOD_ID, name = "Immersive Floofs", version = "1.0.0")
public class ImmersiveFloofs {
    public static final String MOD_ID = "immersivefloofs";

    /** CONFIG **/
    private Configuration config;
    private boolean randomBullet = false;
    private boolean shooterBullet = true;
    private boolean craftedBullet = true;
    private boolean milkResets = true;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());
        shooterBullet = config.getBoolean("Shooter Floof Bullet", Configuration.CATEGORY_GENERAL, true, "This bullet will apply the shooters parts data to the target");
        craftedBullet = config.getBoolean("Crafted Floof Bullet", Configuration.CATEGORY_GENERAL, true, "This bullet will store the crafters parts data on the bullet");
        randomBullet = config.getBoolean("Random Floof Bullet", Configuration.CATEGORY_GENERAL, false, "This bullet will apply random parts from a list");
        milkResets = config.getBoolean("Milk Resets", Configuration.CATEGORY_GENERAL, true, "Whether drinking milk resets the 'effect'");

        if (shooterBullet) BulletHandler.registerBullet("floof_shooter", new FloofBullet() {
            @Override
            public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
                if (Tails.proxy.hasPartsData(shooter.getPersistentID())) {
                    projectile.getEntityData().setString("immersivefloofs", Tails.gson.toJson(Tails.proxy.getPartsData(shooter.getPersistentID())));
                }
                return projectile;
            }
        });
        if (craftedBullet) BulletHandler.registerBullet("floof_crafted", new FloofBullet() {
            @Override
            public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
                if (cartridge.hasTagCompound() && cartridge.getTagCompound().hasKey("immersivefloofs")) {
                    projectile.getEntityData().setString("immersivefloofs", cartridge.getTagCompound().getString("immersivefloofs"));
                }
                return projectile;
            }
        });
        if (randomBullet) BulletHandler.registerBullet("floof_random", new FloofBullet()); // todo

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onMilkDrink(LivingEntityUseItemEvent.Finish e) {
        if (milkResets && e.getItem().getItem() instanceof ItemBucketMilk) {
            // todo reset
        }
    }
}
