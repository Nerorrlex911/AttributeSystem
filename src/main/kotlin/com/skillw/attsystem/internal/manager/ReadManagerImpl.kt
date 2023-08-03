package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.attsystem.api.compiled.oper.StringsCompiledData
import com.skillw.attsystem.api.event.ItemReadEvent
import com.skillw.attsystem.api.event.StringsReadEvent
import com.skillw.attsystem.api.manager.ReadManager
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.attsystem.util.MapUtils.toList
import com.skillw.attsystem.util.MapUtils.toMutableMap
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.chat.uncolored
import taboolib.module.nms.getItemTag
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import java.util.*

object ReadManagerImpl : ReadManager() {
    override val key = "ReadManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem
    private val lores = BaseMap<Int, List<String>>()

    private fun read(
        toRead: String,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData {
        val attributeData = AttributeData()
        for (attribute in AttributeSystem.attributeManager.attributes) {
            val read = attribute.readPattern
            if (read !is ReadGroup<*>) continue
            val status = read.read(toRead, attribute, entity, slot)
            if (status != null) {
                attributeData.operation(attribute, status)
                break
            }
        }
        return attributeData
    }

    override fun read(
        strings: Collection<String>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData? {
        return mirrorIfDebug("read-strings") {
            val restStrings = LinkedList<String>()

            val compiledData = ComplexCompiledData()
            //�Ȱѵ��������Ĺ��˳�������ֹ��������������ȫ������
            for (string in strings) {
                var toRead = string
                if (ASConfig.ignores.any { toRead.uncolored().contains(it) }) continue
                val matcher = ASConfig.lineConditionPattern.matcher(toRead)
                if (!matcher.find()) {
                    restStrings.add(toRead)
                    continue
                }
                val builder: ((AttributeData) -> StringsCompiledData)? = runCatching {
                    val requirements = matcher.group("requirement")
                    val funcs = requirements.split(ASConfig.lineConditionSeparator)
                        .mapNotNull { AttributeSystem.conditionManager.condition(entity, it, slot) }
                    return@runCatching { data: AttributeData ->
                        val lineConditions = StringsCompiledData(data)
                        funcs.map { it(data) }.forEach(lineConditions::putAllCond)
                        lineConditions
                    }
                }.getOrNull()
                if (builder == null) {
                    restStrings.add(toRead)
                    continue
                }
                toRead = matcher.replaceAll("")
                val attributeData = read(toRead, entity, slot)
                compiledData.add(builder.invoke(attributeData))
            }
            for (string in restStrings) {
                val attributeData = read(string, entity, slot)
                AttributeSystem.conditionManager.condition(entity, string, slot)?.let {
                    compiledData.putAll(it(attributeData))
                } ?: compiledData.addition.computeIfAbsent("STRINGS-ATTRIBUTE") { AttributeData() }
                    .combine(attributeData)
            }

            val event = StringsReadEvent(entity, strings, compiledData)
            event.call()
            if (!event.isCancelled) event.compiledData else null
        }
    }

    override fun readItemLore(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData? {
        return if (itemStack.hasLore()) {
            mirrorIfDebug("read-item-lore") {
                val origin = itemStack.itemMeta?.lore ?: return@mirrorIfDebug null
                val hashcode = itemStack.itemMeta?.getProperty<List<String>>("lore").hashCode()
                val lore = lores.computeIfAbsent(hashcode) {
                    origin.map { it.uncolored() }
                }
                read(lore, entity, slot)
            }
        } else null
    }

    override fun readItemsLore(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData {
        val compiledData = ComplexCompiledData()
        for (item: ItemStack in itemStacks) {
            compiledData.add(
                readItemLore(item, entity, slot) ?: continue
            )
        }
        return compiledData
    }

    override fun readMap(
        attrDataMap: MutableMap<String, Any>,
        conditions: Collection<Any>,
        entity: LivingEntity?, slot: String?,
    ): CompiledAttrData {
        return AttributeSystem.conditionManager.conditionNBT(entity, conditions, slot)(attrDataMap)
    }

    override fun readItemNBT(
        itemStack: ItemStack,
        entity: LivingEntity?, slot: String?,
    ): CompiledAttrData? {
        return mirrorIfDebug("read-item-nbt") {
            if (itemStack.isAir()) return@mirrorIfDebug null
            val itemTag = itemStack.getItemTag()
            val attributeDataMap = itemTag["ATTRIBUTE_DATA"]?.asCompound()?.toMutableMap() ?: return@mirrorIfDebug null
            val conditions = itemTag["CONDITION_DATA"]?.asList()?.toList() ?: emptyList()
            readMap(attributeDataMap, conditions, entity, slot)
        }
    }

    override fun readItemsNBT(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?, slot: String?,
    ): CompiledAttrData {
        return mirrorIfDebug("read-item-nbt") {
            val compiledData = ComplexCompiledData()
            for (item: ItemStack in itemStacks) {
                compiledData.add(
                    readItemNBT(item, entity) ?: continue
                )
            }
            compiledData
        }
    }


    override fun readItem(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData? {
        return mirrorIfDebug("read-item") {
            val compiledData = ComplexCompiledData()
            readItemLore(itemStack, entity, slot)?.let { compiledData.putAll(it) }
            readItemNBT(itemStack, entity, slot)?.let { compiledData.add(it) }

            val event = ItemReadEvent(
                entity,
                itemStack,
                compiledData,
                slot
            )
            event.call()

            if (!event.isCancelled) event.compiledData else null
        }
    }


    override fun readItems(

        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData {
        val compiledData = ComplexCompiledData()
        for (item: ItemStack in itemStacks) {
            readItem(item, entity, slot)?.let { compiledData.add(it) }
        }
        return compiledData
    }


}
