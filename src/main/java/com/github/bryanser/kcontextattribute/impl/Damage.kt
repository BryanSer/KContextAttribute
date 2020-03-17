package com.github.bryanser.kcontextattribute.impl

import com.github.bryanser.kcontextattribute.attribute.Attribute
import com.github.bryanser.kcontextattribute.attribute.AttributeContext
import com.github.bryanser.kcontextattribute.attribute.DamageAttribute
import com.github.bryanser.kcontextattribute.attribute.DamageEventContext
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

object PhysicalDamage : Damage(
        "Physical",
        "物理",
        1
)

object FireDamage : Damage(
        "Fire",
        "火焰",
        20
)

object BloodDamage : Damage(
        "Blood",
        "血之",
        20
)

object LightningDamage : Damage(
        "Lightning",
        "闪电",
        20
)

object TrueDamage : Damage(
        "True",
        "真实",
        100
)

class DamageContext(ent: LivingEntity, val dmg: Damage) : AttributeContext(ent) {
    var damage: Double = 0.0
    var defence: Double = 0.0

    override fun copy(): AttributeContext = DamageContext(owner, dmg).also {
        it.damage = damage
        it.defence = defence
    }

    override fun toString(): String {
        if (dmg == TrueDamage) {
            return "§a§l${dmg.displayName}  伤害: ${String.format("%.2f", damage)}"
        }
        return "§a§l${dmg.displayName}  伤害: ${String.format("%.2f", damage)}, 防御: ${String.format("%.2f", defence)}"
    }

}

abstract class Damage(
        name: String,
        displayName: String,
        priority: Int
) : Attribute<DamageContext>(
        name,
        displayName,
        priority
), DamageAttribute {
    val damageKey = "${displayName}伤害[：:] *(?<value>[+-][0-9.]+)".toRegex().toPattern()
    val defenceKey = "${displayName}防御[：:] *(?<value>[+-][0-9.]+)".toRegex().toPattern()

    override fun createContext(p: LivingEntity): DamageContext = DamageContext(p, this)

    override fun loadAttribute(ctx: DamageContext, lore: List<String>, item: ItemStack) {
        if (isWeapon(item)) {
            for (s in lore) {
                val str = ChatColor.stripColor(s)
                val dm = damageKey.matcher(str)
                if (dm.find()) {
                    val value = dm.group("value")
                    ctx.damage += value.toDouble()
                }
            }
        } else if (isArmor(item)) {
            for (s in lore) {
                val str = ChatColor.stripColor(s)
                val m = defenceKey.matcher(str)
                if (m.find()) {
                    val value = m.group("value")
                    ctx.defence += value.toDouble()
                }
            }
        }
    }

    override fun init() {
    }

    override fun onDamage(ctx: DamageEventContext) {
        var dmg = ctx.damager.with(this) {
            damage
        } - ctx.entity.with(this) {
            defence
        }
        if (dmg < 0) {
            dmg = 0.0
        }
        ctx.damage += dmg
    }
}

val weapon: Set<Material> by lazy {
    val set = EnumSet.noneOf(Material::class.java)
    for (m in Material.values()) {
        val name = m.name
        if (name.contains("_SWORD") || name.contains("_AXE") || name.contains("_PICKAXE") || name.contains("_HOE")) {
            set += m
        }
    }
    set
}
val armor: Set<Material> by lazy {
    val set = EnumSet.noneOf(Material::class.java)
    for (m in Material.values()) {
        Material.IRON_BOOTS
        val name = m.name
        if (name.contains("_HELMET") || name.contains("_CHESTPLATE") || name.contains("_LEGGINGS") || name.contains("_BOOTS")) {
            set += m
        }
    }
    set
}

inline fun isWeapon(item: ItemStack): Boolean {
    return item.type in weapon
}

inline fun isArmor(item: ItemStack): Boolean {
    return item.type in armor
}