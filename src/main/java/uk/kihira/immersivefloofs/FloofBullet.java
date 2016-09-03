package uk.kihira.immersivefloofs;

import blusunrize.immersiveengineering.api.tool.BulletHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class FloofBullet implements BulletHandler.IBullet {

    private final ResourceLocation[] texture;

    public FloofBullet(String name) {
        texture = new ResourceLocation[]{new ResourceLocation(ImmersiveFloofs.MOD_ID, name)};
    }

    @Override
    public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
        return projectile;
    }

    @Override
    public void onHitTarget(World world, RayTraceResult target, EntityLivingBase shooter, Entity projectile, boolean headshot) {
        if (projectile.getEntityData().hasKey("immersivefloofs") && target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit instanceof EntityPlayer) {
            PartsData data = Tails.gson.fromJson(projectile.getEntityData().getString("immersivefloofs"), PartsData.class);
            Tails.proxy.addPartsData(target.entityHit.getPersistentID(), data);
            if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
                Tails.networkWrapper.sendToAll(new PlayerDataMessage(target.entityHit.getPersistentID(), data, false));
            }

            FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new TextComponentTranslation("chat.immersivefloofs.floof", target.entityHit.getName(), shooter.getName()));
            world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.PLAYERS, 0.3f, 0.6f);
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
