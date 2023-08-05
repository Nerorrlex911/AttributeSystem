package com.skillw.attsystem.util.nms

import com.skillw.attsystem.util.BukkitAttribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity
import org.bukkit.entity.LivingEntity
import taboolib.module.nms.MinecraftVersion


/**
 * @className NMS
 *
 * @author Glom
 * @date 2022/8/9 22:24 Copyright 2022 user. All rights reserved.
 */
class NMSImpl : NMS() {
    override fun getAttribute(entity: LivingEntity, attribute: BukkitAttribute): AttributeInstance? {
        return if (MinecraftVersion.major <= 4) {
            val craftAttributes = (entity as CraftLivingEntity).handle.craftAttributes
            val bukkitAtt = attribute.toBukkit() ?: return null
            craftAttributes.getAttribute(bukkitAtt)
        } else entity.getAttribute(attribute.toBukkit() ?: return null)
    }

}