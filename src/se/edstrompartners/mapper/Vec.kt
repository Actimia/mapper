package se.edstrompartners.mapper

import java.util.Comparator.comparingDouble


class Vec(val x: Double, val y: Double) : Comparable<Vec>, Shape {
    operator fun plus(o: Vec) = Vec(x + o.x, y + o.y)

    operator fun minus(o: Vec) = Vec(x - o.x, y - o.y)

    operator fun times(scale: Double) = Vec(x * scale, y * scale)

    operator fun div(scale: Double) = Vec(x / scale, y / scale)

    fun dot(o: Vec): Double = x * o.x + y * o.y

    fun angle(o: Vec): Double {
        return Math.acos(dot(o) / (len() * o.len()))
    }

    override operator fun compareTo(other: Vec): Int {
        return comparator.compare(this, other)
    }

    operator fun unaryMinus(): Vec = Vec(-x, -y)

    operator fun component1(): Double = x
    operator fun component2(): Double = y

    fun unit() = this / len()
    fun normal() = Vec(-y, x)
    fun len() = Math.sqrt(len2())
    fun theta() = Math.atan2(y, x)
    private fun len2() = x * x + y * y
    fun dist(o: Vec) = (this - o).len()
    fun isWithin(o: Vec, dist: Double): Boolean {
        return (this - o).len2() < dist * dist
    }

    override val edges: List<Line>
        get() = listOf()
    override val corners: List<Vec>
        get() = listOf(this)

    override fun contains(point: Vec): Boolean {
        return this == point
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return 31 * x.hashCode() + y.hashCode()
    }

    override fun toString(): String = String.format("(%.1f, %.1f)", x, y)

    companion object {
        val zero = Vec(0.0, 0.0)
        private val comparator = comparingDouble<Vec> { it.len2() }.thenComparingDouble { it.theta() }
    }

}