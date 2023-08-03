package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Entity
import java.util.*

/**
 * Fight status manager
 *
 * @constructor Create empty Fight status manager
 */
abstract class FightStatusManager : Manager {
    /**
     * �Ƿ���ս��״̬
     *
     * @param uuid
     * @return �Ƿ���ս��״̬
     */
    abstract fun isFighting(uuid: UUID): Boolean

    /**
     * �Ƿ���ս��״̬
     *
     * @param entity
     * @return �Ƿ���ս��״̬
     */
    abstract fun isFighting(entity: Entity): Boolean

    /**
     * ��ʵ�����ս��״̬
     *
     * @param entity
     */
    abstract fun intoFighting(entity: Entity)

    /**
     * ��ʵ�����ս��״̬
     *
     * @param uuid
     */
    abstract fun intoFighting(uuid: UUID)

    /**
     * ��ʵ������ս��״̬
     *
     * @param entity
     */
    abstract fun outFighting(entity: Entity)

    /**
     * ��ʵ������ս��״̬
     *
     * @param uuid
     */
    abstract fun outFighting(uuid: UUID)

}
