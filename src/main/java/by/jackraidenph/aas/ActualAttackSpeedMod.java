package by.jackraidenph.aas;

import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aas")
public class ActualAttackSpeedMod {
    private static final Logger LOGGER = LogManager.getLogger();
    private int index;
    private String original;
    private DecimalFormat df2 = new DecimalFormat("#.###");

    public ActualAttackSpeedMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private double sumAllAndReturn(Collection<AttributeModifier> col) {
        return col.stream().map(AttributeModifier::getAmount).reduce(0.0D, Double::sum);
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        e.getToolTip().forEach(x -> {
            if (x.getString().contains(I18n.format("attribute.name.generic.attackSpeed"))) {
                index = e.getToolTip().indexOf(x);
                original = x.getString();
            }
        });
        try {
            Multimap<String, AttributeModifier> map = e.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND);
            double attackSpeed = 4 + sumAllAndReturn(map.get("generic.attackSpeed"));
            double damage = 1 + sumAllAndReturn(map.get("generic.attackDamage"));
            double actualSpeedInSeconds = Math.ceil((20 / attackSpeed)) / 20;
            e.getToolTip().set(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.time", df2.format(actualSpeedInSeconds).trim()) + " (" + original.trim() + ")"));
            e.getToolTip().add(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.dps", df2.format(damage / actualSpeedInSeconds))));
        } catch (Exception ignored) {
        }
    }
}
