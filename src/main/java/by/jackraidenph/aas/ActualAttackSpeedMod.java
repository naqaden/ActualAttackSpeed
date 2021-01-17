package by.jackraidenph.aas;

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

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aas")
public class ActualAttackSpeedMod {
    private static final Logger LOGGER = LogManager.getLogger();
    private int index;
    private String original;

    public ActualAttackSpeedMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e) {
        e.getToolTip().forEach(x -> {
            if (x.getFormattedText().contains(I18n.format("attribute.name.generic.attackSpeed"))) {
                index = e.getToolTip().indexOf(x);
                original = x.getString();
            }
        });
        try {
            double attackSpeed = 4 + e.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackSpeed").stream().map(AttributeModifier::getAmount).reduce(0.0D, Double::sum);
            double damage = 1 + e.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get("generic.attackDamage").stream().findFirst().get().getAmount();
            DecimalFormat df2 = new DecimalFormat("#.###");
            e.getToolTip().set(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.time", df2.format(1 / attackSpeed)).trim() + " (" + original.trim() + ")"));
            e.getToolTip().add(index, new StringTextComponent(TextFormatting.DARK_GREEN + " " + I18n.format("cooldown.dps", df2.format(damage * attackSpeed))));
        } catch (Exception ignored) {
        }
    }
}
