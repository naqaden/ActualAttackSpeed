package by.jackraidenph.aas;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Scanner;

@Mod(modid = ActualAttackSpeedMod.MODID, name = ActualAttackSpeedMod.NAME, version = ActualAttackSpeedMod.VERSION)
public class ActualAttackSpeedMod {
    public static final String MODID = "aas";
    public static final String NAME = "Actual Attack Speed";
    public static final String VERSION = "1.0.0";

    public static Logger MOD_LOGGER;
    private int index;
    private String original;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MOD_LOGGER = event.getModLog();
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        e.getToolTip().forEach(x -> {
            if (x.contains(I18n.format("attribute.name.generic.attackSpeed"))) {
                index = e.getToolTip().indexOf(x);
                original = x;
            }
        });
        try {
            double attackSpeed = 4 + e.getItemStack().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get("generic.attackSpeed").stream().map(AttributeModifier::getAmount).reduce(0.0D, Double::sum);
            double damage = 1 + e.getItemStack().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get("generic.attackDamage").stream().findFirst().get().getAmount();
            DecimalFormat df2 = new DecimalFormat("#.###");
            e.getToolTip().set(index, " " + I18n.format("cooldown.time", df2.format(1 / attackSpeed)).trim() + " (" + original.trim() + ")");
            e.getToolTip().add(index, " " + I18n.format("cooldown.dps", df2.format(damage * attackSpeed)));
        } catch (Exception ignored) {
        }
    }
}
