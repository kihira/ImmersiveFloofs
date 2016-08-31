package uk.kihira.immersivefloofs;

import blusunrize.immersiveengineering.api.tool.BulletHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class FloofBullet implements BulletHandler.IBullet {

    private static final ResourceLocation[] texture = new ResourceLocation[]{new ResourceLocation("immersivefloofs", "textures/bullet_floof.png")};

    @Override
    public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
        // Use players data if none set on the cartridge
        if (cartridge.hasTagCompound() && !cartridge.getTagCompound().hasKey("immersivefloofs") && Tails.proxy.hasPartsData(shooter.getPersistentID())) {
            projectile.getEntityData().setString("immersivefloofs", Tails.gson.toJson(Tails.proxy.getPartsData(shooter.getPersistentID())));
        }
        return projectile;
    }

    @Override
    public void onHitTarget(World world, RayTraceResult target, EntityLivingBase shooter, Entity projectile, boolean headshot) {
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit instanceof EntityPlayer) {
            PartsData data = Tails.gson.fromJson(projectile.getEntityData().getString("immersivefloofs"), PartsData.class);
            Tails.proxy.addPartsData(target.entityHit.getPersistentID(), data);
            if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
                Tails.networkWrapper.sendToAll(new PlayerDataMessage(target.entityHit.getPersistentID(), data, false));
            }
        }
    }

    @Override
    public ItemStack getCasing(ItemStack stack) {
        return BulletHandler.emptyCasing;
    }

    @Override
    public ResourceLocation[] getTextures() {
        return texture;
    }

    @Override
    public int getColour(ItemStack stack, int layer) {
        return 0xffffffff;
    }
}
