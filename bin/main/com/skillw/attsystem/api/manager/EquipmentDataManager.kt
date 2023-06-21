package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Equipment data manager
 *
 * @constructor Create empty Equipment data manager
 */
abstract class EquipmentDataManager : BaseMap<UUID, EquipmentDataCompound>(), Manager {


    /**
     * 更新实体装备数据集
     *
     * @param entity 实体
     * @return 装备数据集
     */
    abstract fun update(entity: LivingEntity): EquipmentDataCompound?

    /**
     * Read item lore
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemLore(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData?


    /**
     * Read items lore
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData?

    /**
     * Read item NBT
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemNBT(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound?

    /**
     * Read items n b t
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemsNBT(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound?


    /**
     * Read item
     *
     * 读取物品的属性数据集(lore & NBT)
     *
     * 触发ItemReadEvent
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品属性数据集
     */
    abstract fun readItem(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound


    /**
     * Read items
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品属性数据集
     */
    abstract fun readItems(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound


    /**
     * Add equipment
     *
     * @param entity 实体
     * @param key 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipment(
        entity: LivingEntity,
        key: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param entity 实体
     * @param key 键(源)
     * @param equipments 装备数据
     * @return 装备数据
     */
    abstract fun addEquipment(
        entity: LivingEntity, key: String, equipments: EquipmentData,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipment(
        uuid: UUID, key: String, equipments: Map<String, ItemStack>,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     */
    abstract fun addEquipment(
        uuid: UUID, key: String, equipmentData: EquipmentData,
    ): EquipmentData

    /**
     * Remove equipment
     *
     * @param entity 实体
     * @param key 键(源)
     */
    abstract fun removeEquipment(entity: LivingEntity, key: String)

    /**
     * Remove equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     */
    abstract fun removeEquipment(uuid: UUID, key: String)
}
