package by.jackraidenph.aas;

import com.google.common.collect.Multimap;
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
import java.util.Collection;

@Mod(modid = ActualAttackSpeedMod.MODID, name = ActualAttackSpeedMod.NAME, version = ActualAttackSpeedMod.VERSION)
public class ActualAttackSpeedMod {
    public static final String MODID = "aas";
    public static final String NAME = "Actual Attack Speed";
    public static final String VERSION = "1.1.0";
    public static Logger MOD_LOGGER;
    private DecimalFormat df2 = new DecimalFormat("#.###");

    private double sumAllAndReturn(Collection<AttributeModifier> col) {
        return col.stream().map(AttributeModifier::getAmount).reduce(0.0D, Double::sum);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MOD_LOGGER = event.getModLog();
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        for (String tipLine : e.getToolTip()) {
            if (tipLine.contains(I18n.format("attribute.name.generic.attackSpeed"))) {
                int index = e.getToolTip().indexOf(tipLine);
                Multimap<String, AttributeModifier> map = e.getItemStack().getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
                double attackSpeed = 4 + sumAllAndReturn(map.get("generic.attackSpeed"));
                double damage = 1 + sumAllAndReturn(map.get("generic.attackDamage"));
                double actualSpeedInSeconds = Math.ceil((20 / attackSpeed)) / 20;
                e.getToolTip().set(index, " " + I18n.format("cooldown.time", df2.format(actualSpeedInSeconds).trim()) + " (" + tipLine.trim() + ")");
                e.getToolTip().add(index, " " + I18n.format("cooldown.dps", df2.format(damage / actualSpeedInSeconds)));
                break;
            }
        }
    }
}
