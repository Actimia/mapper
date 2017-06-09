package se.edstrompartners.mapper

class Line(val a: Vec, val b: Vec) {

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
}





