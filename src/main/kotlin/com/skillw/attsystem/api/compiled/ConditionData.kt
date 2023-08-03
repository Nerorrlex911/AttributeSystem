package com.skillw.attsystem.api.compiled

import com.skillw.attsystem.api.condition.Condition
import com.skillw.pouvoir.api.able.Keyable
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * @className ConditionData
 *
 * @author Glom
 * @date 2023/8/2 16:47 Copyright 2023 user. All rights reserved.
 */
class ConditionData(override val key: Condition) : Keyable<Condition>, ConfigurationSerializable {
    private val parameters = LinkedList<Map<String, Any>>()

    fun push(map: Map<String, Any>): ConditionData {
        parameters.add(map)
        return this
    }

    fun condition(entity: LivingEntity?): Boolean {
        return parameters.all {
            key.condition(entity, it)
        }
    }

    fun addAll(other: ConditionData): ConditionData {
        if (other.key != key) return this
        other.parameters.forEach(this::push)
        return this
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(key.toString() to parameters)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConditionData) return false
        return key == other.key
    }


}