package se.edstrompartners.mapper

class DelauneyTriangulation private constructor(val triangles: List<Triangle>) {

    class Triangle(val x: Vec, val y: Vec, val z: Vec) {
        val centroid = Vec((x.x + y.x + z.x) / 3, (x.y + y.y + z.y) / 3)
        val vertices = listOf(x, y, z).sortedBy { (centroid - it).theta() }

        val circumpoint: Vec = computeCircumpunct()
        val circumdistance: Double = computeCircumdistance()

        private fun computeCircumpunct(): Vec {
            val p1 = (x + y) * 0.5
            val p2 = p1 + (x - y).normal()

            val p3 = (y + z) * 0.5
            val p4 = p3 + (y - z).normal()

            val l1 = Line(p1, p2)
            val l2 = Line(p3, p4)
            return l1.intersection(l2)!! // parallel iff two vertices are identical, which isn't really a triangle
        }

        private fun computeCircumdistance() = x.dist(circumpoint)

        fun insideCircumcircle(p: Vec): Boolean {
            return p.dist(circumpoint) < circumdistance
        }

        override fun toString(): String = "$vertices"

        fun edges(): List<Line> = listOf(
            Line(vertices[0], vertices[1]),
            Line(vertices[1], vertices[2]),
            Line(vertices[2], vertices[0])
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Triangle) return false
            return this.x == other.x && this.y == other.y && this.z == other.z
        }

        override fun hashCode(): Int {
            return 17 + 31 * x.hashCode() + 31 * y.hashCode() + 31 * z.hashCode()
        }


    }

    fun toGraph(): Graph<Vec, Line> {
        val res = Graph<Vec, Line>()
        triangles.forEach { tri ->
            tri.vertices.filter { it !in res }.forEach { res.addNode(it) }
            tri.edges().forEach { res.addEdge(it.a, it.b, it) }
        }
        return res
    }

    fun toTriGraph(): Graph<Triangle, Line> {
        val res = Graph<Triangle, Line>()
        triangles.forEach { res.addNode(it) }
        triangles.forEach { tri ->
            triangles.filter { it.vertices.minus(tri.vertices).size == 2 }
                .forEach {
                    val lines = tri.edges() - it.edges()
                    res.addEdge(tri, it, lines[0])
                }
        }

        return res
    }

    companion object {
        fun create(points: List<Vec>, world: World): DelauneyTriangulation {
            val margin = 5.0
            val superTriangle = Triangle(
                -Vec(margin,margin),
                Vec(-margin, world.height * 2 + 2 * margin),
                Vec(world.width * 2 + 2 * margin, -margin))
            val tris = mutableListOf(superTriangle)
            for (point in points) {
                val badTriangles = tris.filter { it.insideCircumcircle(point) }.toSet()
                val polygon = mutableSetOf<Line>()
                for (bad in badTriangles) {
                    for (edge in bad.edges()) {
                        if (!badTriangles.filter { it != bad }.flatMap { it.edges() }.contains(edge))
                            polygon.add(edge)
                    }
                }
                tris.removeAll(badTriangles)
                polygon.mapTo(tris) { (a,b) -> Triangle(point, a, b) }
            }
            val superVertices = superTriangle.vertices
            return DelauneyTriangulation(tris.filter { it.vertices.none { it in superVertices } })
        }
    }
}
