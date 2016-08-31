package uk.kihira.immersivefloofs;

import blusunrize.immersiveengineering.api.tool.BulletHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "immersivefloofs", name = "Immersive Floofs", version = "1.0.0")
public class ImmersiveFloofs {

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        BulletHandler.registerBullet("floofbullet", new FloofBullet());
    }
}
