package se.edstrompartners.mapper

interface Shape {
    val edges: List<Line>
    val corners: List<Vec>

    operator fun contains(point: Vec): Boolean

    operator fun contains(other: Shape): Boolean = other.corners.all { it in this }
}

class Line(val a: Vec, val b: Vec) : Shape{

    override val edges: List<Line>
        get() = listOf(this)
    override val corners: List<Vec>
        get() = listOf(a,b)

    override fun contains(point: Vec): Boolean {
        val (a,b) = this.a
        val (d,e) = point
        val (g,h) = this.b
        // the three points are colinear iff this determinant is 1
        return a*e + b*g + d*h - e*g - b*d - a*h == 1.0
    }

    operator fun component1(): Vec = a
    operator fun component2(): Vec = b

    fun intersection(o: Line): Vec? {
        val p1 = a
        val p2 = b
        val p3 = o.a
        val p4 = o.b
        val denominator = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x)
        if (denominator == 0.0) return null
        val ix = ((p1.x * p2.y - p1.y * p2.x) * (p3.x - p4.x) - (p1.x - p2.x) * (p3.x * p4.y - p3.y * p4.x)) / denominator
        val iy = ((p1.x * p2.y - p1.y * p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x * p4.y - p3.y * p4.x)) / denominator
        return Vec(ix, iy)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Line) return false
        return (this.a == other.a && this.b == other.b) || (this.a == other.b && this.b == other.a)
    }

    override fun hashCode(): Int {
        return 17 + a.hashCode() + b.hashCode()
    }

    override fun toString(): String {
        return "($a <-> $b)"
    }

    fun center(): Vec {
        return (a + b) / 2.0
    }

    fun clipTo(rect: Rect) : Line? {
        // Both points are inside
        if (a in rect && b in rect) return this
        // neither point is inside
        if (a !in rect && b !in rect) return null

        // one of the points are inside
        val origin = if (a in rect) a else b
        val target = rect.edges.map { it.intersection(this) }
                .filterNotNull()
                .minBy { (origin - it).len() }!!

        return Line(origin, target)
    }
}

class Rect(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
) : Shape {

    init {
        if (width <= 0) throw IllegalArgumentException("Width must be positive")
        if (height <= 0) throw IllegalArgumentException("Height must be positive")
    }

    constructor(topLeft: Vec, size: Vec) : this(topLeft.x, topLeft.y, size.x, size.y)

    override val corners: List<Vec> = listOf(
        Vec(x, y),
        Vec(x, y + height),
        Vec(x + width, y + height),
        Vec(x + width, y)
    )

    override val edges: List<Line> = listOf(
        Line(corners[0], corners[1]),
        Line(corners[1], corners[2]),
        Line(corners[2], corners[3]),
        Line(corners[3], corners[0])
    )

    override fun contains(point: Vec): Boolean {
        return point.x in x..x + width && point.y in y..y + height
    }

}


