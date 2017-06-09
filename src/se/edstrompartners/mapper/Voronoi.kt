package se.edstrompartners.mapper

import java.util.*

class Polygon(val points: List<Vec>) {

    val edges: List<Line> get() {
        return listOf()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Polygon) return false
        return this.points == other.points
    }

    override fun hashCode(): Int {
        return 17 + points.hashCode()
    }
}

class VoronoiDiagram private constructor(val world: World) : Graph<Vec, Line>() {

    fun getPolygon(node: Vec): Polygon {
        val closestCorner = world.bounds.corners.sortedBy { it.dist(node) }.first()
        val corner = nodes.sortedBy { it.dist(closestCorner) }.first()



        val lines = getEdges(node).map { it.clipTo(world.bounds) }.filterNotNull()
        val (a, b) = lines.first()

        val poly = ArrayDeque<Vec>()
        poly.add(a)
        poly.add(b)
        for (i in 0..lines.size) {
            val start = poly.first
            val end = poly.last
            lines.filter { (a, b) -> poly.contains(a) xor poly.contains(b) }
                .forEach { (a, b) ->
                    if (a == start) poly.addFirst(b)
                    if (a == end) poly.addLast(b)
                    if (b == start) poly.addFirst(a)
                    if (b == end) poly.addLast(a)
                }
        }
        if (corner == node) {
            poly.addLast(closestCorner)
        }
        return Polygon(poly.toList())
    }


    fun averageDistance(): Double {
        return nodes.map { node ->
            getNeighbors(node).map { it.dist(node) }.average()
        }.average()
    }

    companion object {
        fun createFromDelauney(delauney: DelauneyTriangulation): Graph<Vec, Line> {
            val tris = delauney.triangles
            val res = Graph<Vec, Line>()

            tris.forEach {
                res.addNode(it.circumpoint)
            }

            for (tri in tris) {
                for (edge in tri.edges()) {
                    tris.filter { it != tri }
                        .filter { it.edges().contains(edge) }
                        .forEach { res.addEdge(tri.circumpoint, it.circumpoint, Line(tri.circumpoint, it.circumpoint)) }
                }
            }

            return res
        }

        fun createFromDelauney2(delauney: DelauneyTriangulation, world: World): VoronoiDiagram {
            val graph = delauney.toGraph()
            val triangles = delauney.triangles
            val res = VoronoiDiagram(world)

            for (node in graph.nodes) {
                res.addNode(node)
            }

            for (edge in graph.edges) {
                val tris = triangles.filter { it.edges().contains(edge) }
                when (tris.size) {
                    1 -> {
                        // edge case, literally
                        val x = tris.first().circumpoint
                        if (x in world.bounds) {
                            val y = edge.center()
                            val cand = world.closestIntersectionWithEdge(Line(x, y))
                            res.addEdge(edge.a, edge.b, Line(x, cand))
                        }
                    }
                    2 -> {
                        val x = tris[0].circumpoint
                        val y = tris[1].circumpoint
                        val cand = Line(x, y).clipTo(world.bounds)
                        if (cand != null) {
                            res.addEdge(edge.a, edge.b, cand)
                        }
                    }
                }
            }

            return res
        }
    }
}
