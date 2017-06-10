package se.edstrompartners.mapper

import java.awt.Color
import java.awt.Graphics2D

class Cell(val world: World, val midpoint: Vec, val polygon: Polygon) {

    val elevation = world.normal(midpoint)
    val humidity = world.noise(midpoint)

    val color: Color = HSV(clamp(humidity), 0.8, clamp(elevation)).color()


    fun draw(g: Graphics2D) {
//        g.color = Color.green
        g.color = color
        g.fillPolygon(polygon)
    }
}

class Map private constructor(val graph: Graph<Cell, Line>, val world: World) {


    fun draw(g: Graphics2D) {
        g.color = HSV(0.55, 1.0, 0.8).color()
        graph.nodes.forEach { it.draw(g) }
        g.color = Color.black
//        graph.edges.forEach { g.drawLine(it) }
    }

    companion object {
        fun create(voronoi: VoronoiDiagram): Map {
            val graph = voronoi.mapNodes { Cell(voronoi.world, it, voronoi.getPolygon(it)) }
            return Map(graph, voronoi.world)
        }
    }

}
