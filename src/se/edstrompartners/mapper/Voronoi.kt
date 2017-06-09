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

class VoronoiDiagram private constructor() : Graph<Vec, Line>() {

    fun getPolygon(node: Vec) : Polygon {
        fun ccw(a : Vec, b : Vec, c : Vec) : Double {
            val (x1,y1) = a
            val (x2,y2) = b
            val (x3,y3) = c
            return (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1)
        }
        val lines = getEdges(node)
        val (a,b) = lines.first()

        val poly = ArrayDeque<Vec>()
        poly.add(a)
        poly.add(b)
        for (i in 0..lines.size) {
//        while (poly.size <= lines.size) {
            val start = poly.first
            val end = poly.last
//            println(poly)
            lines.filter { (a,b) -> poly.contains(a) xor poly.contains(b) }
                .forEach { (a,b) ->
                    if (a == start) poly.addFirst(b)
                    if (a == end) poly.addLast(b)
                    if (b == start) poly.addFirst(a)
                    if (b == end) poly.addLast(a)
                }
        }

//        val lines = mutableListOf<Line>()
//        for (edge in edges) {
//            if (ccw(node, edge.a, edge.b) >= 0) {
//                lines.add(edge)
//            } else {
//                lines.add(Line(edge.b, edge.a))
//            }
//        }
//        val poly = edges.flatMap{listOf(it.a, it.b)}.toSet().toList()
//        val poly = lines.sortedBy { ccw(node, it.a, it.b) }.flatMap { listOf(it.a, it.b) }.toSet().toList()
        return Polygon(poly.toList())
    }

//    fun getPolygon(node : Vec) : Polygon {
//        val lines = getEdges(node)
//        val verts = lines.flatMap { listOf(it.a, it.b) }.toSet().sortedBy { it.y }.toMutableList()
//        verts.add(verts.first())
//
//        fun ccw(a : Vec, b : Vec, c : Vec) : Double {
//            val (x1,y1) = a
//            val (x2,y2) = b
//            val (x3,y3) = c
//            return (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1)
//        }
//
//        for (i in 2..verts.size)
//
//        return Polygon(poly)
//        val lines = getEdges(node).toMutableList()
//        val (a1,a2) = lines.removeAt(lines.size - 1)
//        val poly = ArrayDeque<Vec>()
//        poly.add(a1)
//        poly.add(a2)
//        println("new polygon: ${lines.size}")
//        println("lines: $lines")
//        while(poly.size < lines.size) {
//            for (line in lines) {
//                val (x,y) = line
//                val start = poly.first
//                val end = poly.last
//                println("current: $poly")
//                println("cand: $x $y")
//                if (x == start && y !in poly) {
//                    poly.addFirst(y)
//                } else if (x == end && y !in poly) {
//                    poly.addLast(y)
//                } else if (y == start && x !in poly) {
//                    poly.addFirst(x)
//                } else if (y == end && x !in poly) {
//                    poly.addLast(x)
//                }
//            }
//        }
//        println("final: $poly")
//        return Polygon(poly.toList())
//    }

    fun averageDistance() : Double {
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
            val res = VoronoiDiagram()

            for (node in graph.nodes) {
                res.addNode(node)
            }

            for (edge in graph.edges) {
                val nodes = graph.getNodes(edge)
                val tris = triangles.filter { it.edges().contains(edge) }
                when (tris.size) {
                    1 -> {
                        // edge case, literally
                        val x = tris.first().circumpoint
                        val y = edge.center()
                        val cand = Line(x,y)
//                        res.addEdge(edge.a, edge.b, cand)
                        val closestEdge = world.closestIntersectionWithEdge(cand)
                        res.addEdge(edge.a, edge.b, Line(x,closestEdge))
                    }
                    2 -> {
                        val x = tris[0].circumpoint
                        val y = tris[1].circumpoint
                        res.addEdge(edge.a, edge.b, Line(x, y))
                    }
                }
            }

            return res
        }
    }
}
