package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageEventContext(
        val damager: EntityContext,
        val entity: EntityContext,
        val event: EntityDamageByEntityEvent
) {
    var damage = event.damage

}

