package com.github.bryanser.kcontextattribute.impl

import com.github.bryanser.kcontextattribute.attribute.Attribute
import com.github.bryanser.kcontextattribute.attribute.AttributeContext
import com.github.bryanser.kcontextattribute.attribute.DamageAttribute
import com.github.bryanser.kcontextattribute.attribute.DamageEventContext
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity

class DamageContext(ent: LivingEntity, val dmg: Damage) : AttributeContext(ent) {
    var damage: Double = 0.0
    var defence: Double = 0.0

    override fun copy(): AttributeContext = DamageContext(owner, dmg).also {
        it.damage = damage
        it.defence = defence
    }

    override fun toString(): String {
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

    override fun loadAttribute(ctx: DamageContext, lore: List<String>) {
        for (s in lore) {
            val str = ChatColor.stripColor(s)
            val dm = damageKey.matcher(str)
            if (dm.find()) {
                val value = dm.group("value")
                ctx.damage += value.toDouble()
            } else {
                val m = defenceKey.matcher(str)
                if (m.find()) {
                    val value = dm.group("value")
                    ctx.defence += value.toDouble()
                }
            }
        }
    }

    override fun init() {
    }

    override fun onDamage(ctx: DamageEventContext) {
    }
}