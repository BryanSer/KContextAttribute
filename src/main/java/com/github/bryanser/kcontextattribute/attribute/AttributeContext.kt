package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.entity.LivingEntity

abstract class AttributeContext(
        val owner: LivingEntity
) {

    abstract fun copy(): AttributeContext

    abstract override fun toString():String
}