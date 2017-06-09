package se.edstrompartners.mapper

import java.util.*

class World(val width: Double, val height: Double) {
    val size = Vec(width, height)

    val topLeft = Vec.zero
    val topRight = topLeft + Vec(size.x, 0.0)
    val bottomLeft = topLeft + Vec(0.0, size.y)
    val bottomRight = topLeft + size

    val edges = listOf(
        Line(topLeft, topRight),
        Line(topRight, bottomRight),
        Line(bottomRight, bottomLeft),
        Line(bottomLeft, topLeft)
    )

    fun closestIntersectionWithEdge(line : Line) : Vec {
        return edges.map { it.intersection(line) }
            .filterNotNull()
            .minBy { (line.a - it).len() }!!
    }

    fun getRandomPoint(rnd : Random = Random()): Vec {
        return topLeft + Vec(size.x * rnd.nextDouble(), size.y * rnd.nextDouble())
    }

}