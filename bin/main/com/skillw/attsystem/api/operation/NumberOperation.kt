package com.skillw.attsystem.api.operation

/**
 * Base operation
 *
 * @constructor Create empty Base operation
 * @property key 运算操作键
 */
abstract class NumberOperation(override val key: String) : Operation<Number> {
    abstract override fun operate(a: Number, b: Number): Number

    override var release: Boolean = false

    /**
     * 构成运算元素 (可以理解成 运算符 + 数)
     *
     * @param number
     * @return 运算元素
     */
    fun element(number: Number): OperationElement = OperationElement(this, number)

}