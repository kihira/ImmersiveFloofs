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

import java.util.HashMap;
import java.util.UUID;

public class FloofBullet implements BulletHandler.IBullet {
    public static final HashMap<UUID, PartsData> oldPartCache = new HashMap<>();

    private final ResourceLocation[] textures;

    public FloofBullet(ResourceLocation[] textures) {
        this.textures = textures;
    }

    @Override
    public Entity getProjectile(EntityPlayer shooter, ItemStack cartridge, Entity projectile, boolean charged) {
        return projectile;
    }

    @Override
    public void onHitTarget(World world, RayTraceResult target, EntityLivingBase shooter, Entity projectile, boolean headshot) {
        if (projectile.getEntityData().hasKey("immersivefloofs") && target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit instanceof EntityPlayer) {
            UUID targetUUID = target.entityHit.getPersistentID();
            // Store old data for restoration
            if (!oldPartCache.containsKey(targetUUID)) {
                oldPartCache.put(targetUUID, Tails.proxy.getPartsData(targetUUID));
            }

            PartsData data = Tails.gson.fromJson(projectile.getEntityData().getString("immersivefloofs"), PartsData.class);
            Tails.proxy.addPartsData(targetUUID, data);
            if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
                Tails.networkWrapper.sendToAll(new PlayerDataMessage(targetUUID, data, false));
            }

            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendChatMsg(new TextComponentTranslation("chat.immersivefloofs.floofed", target.entityHit.getName(), shooter.getName()));
            world.playSound(null, target.entityHit.posX, target.entityHit.posY, target.entityHit.posZ, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.PLAYERS, 0.3f, 0.6f);
        }
    }

    @Override
    public ItemStack getCasing(ItemStack stack) {
        return BulletHandler.emptyCasing;
    }

    @Override
    public ResourceLocation[] getTextures() {
        return textures;
    }

    @Override
    public int getColour(ItemStack stack, int layer) {
        return 0xffffffff;
    }
}
