package by.jackraidenph.aas;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
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

    private double sumAllAndReturn(Collection<AttributeModifier> col){
        return col.stream().map(AttributeModifier::getAmount).reduce(0.0D, Double::sum);
    }

    public ActualAttackSpeedMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        e.getToolTip().forEach(x -> {
            if (x.getString().contains(I18n.format("attribute.name.generic.attack_speed"))) {
                index = e.getToolTip().indexOf(x);
                original = x.getString();
            }
        });
        try {
            Multimap<Attribute, AttributeModifier> map = e.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND);
            double attackSpeed = Attributes.ATTACK_SPEED.getDefaultValue() + sumAllAndReturn(map.get(Attributes.ATTACK_SPEED));
            double damage = e.getPlayer().getAttributeValue(Attributes.ATTACK_DAMAGE) + sumAllAndReturn(map.get(Attributes.ATTACK_DAMAGE));
            double actualSpeedInSeconds = Math.ceil((20 / attackSpeed)) / 20;
            e.getToolTip().set(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.time", df2.format(actualSpeedInSeconds).trim()) + " (" + original.trim() + ")"));
            e.getToolTip().add(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.dps", df2.format(damage / actualSpeedInSeconds))));
        } catch (Exception ignored) {
        }
    }
}
