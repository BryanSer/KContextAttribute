package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageContext(
        val damager: Context,
        val entity: Context,
        val event: EntityDamageByEntityEvent
) {

}

