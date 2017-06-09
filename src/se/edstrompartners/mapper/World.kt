package se.edstrompartners.mapper

import java.util.*

class World(val width: Double, val height: Double) {
    val bounds = Rect(0.0, 0.0, width, height)

    fun closestIntersectionWithEdge(line: Line): Vec {
        return bounds.edges.map { it.intersection(line) }
            .filterNotNull()
            .minBy { (line.a - it).len() }!!
    }

    fun getRandomPoint(rnd: Random = Random()): Vec {
        return Vec(bounds.width * rnd.nextDouble(), bounds.height * rnd.nextDouble())
    }

}