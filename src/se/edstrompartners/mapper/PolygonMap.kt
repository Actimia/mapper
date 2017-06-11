package se.edstrompartners.mapper

import java.awt.Color
import java.awt.Graphics2D

class Cell(val world: World, val midpoint: Vec, val polygon: Polygon) {

    val elevation = clamp(world.noise(midpoint)/2 + 0.5)
    val humidity = clamp(world.noise(-midpoint) + 0.5)

    var biome : Biome? = null

    companion object {
        val seaLevel = 0.35
        val landGradient = Gradient(HSV(0.1, 0.5, 0.8), HSV(0.6, 0.5, 0.8))
        fun hsvSaturation(elevation: Double) : Double {
            return 1.0 - elevation
//            return 0.8
        }
    }


    fun draw(g: Graphics2D) {
//        g.color = Color.green
        g.color = getColor()
        g.fillPolygon(polygon)
    }

    fun getColor() : Color {
        val h = biome?.hue ?: 0.0
        val s = 1.0 - elevation

        val v = 0.8 + world.noise(midpoint, 5) / 25
        val hsv = HSV(h,s,v)
        return hsv.color()
    }
}

enum class Biome(val hue : Double) {
    WATER(0.55), BEACH(0.13), FOREST(0.4), MOUNTAIN(0.95)
}

class Map private constructor(val graph: Graph<Cell, Line>, val world: World) {

    init {
        graph.nodes
            .filter { it.elevation < Cell.seaLevel }
            .forEach { it.biome = Biome.WATER }

        graph.nodes
            .filter { it.biome == Biome.WATER }
            .flatMap { graph.getNeighbors(it) }
            .filter { it.biome == null }
            .forEach { it.biome = Biome.BEACH }

        graph.nodes
            .filter {it.biome == null}
            .filter {it.elevation < 0.7 }
            .forEach { it.biome = Biome.FOREST }

        graph.nodes
            .filter {it.biome == null}
            .forEach { it.biome = Biome.MOUNTAIN }
    }

    fun draw(g: Graphics2D) {
        graph.nodes.forEach { it.draw(g) }
        g.color = Color(0,0,0)
//        graph.edges.forEach { g.drawLine(it) }
        val maxima = graph.nodes.filter { node ->
            graph.getNeighbors(node).all { it.elevation < node.elevation }
        }.forEach { g.drawPolygon(it.polygon) }
//        g.drawPolygon(highest.polygon)
    }

    companion object {
        fun create(voronoi: VoronoiDiagram): Map {
            val graph = voronoi.mapNodes { Cell(voronoi.world, it, voronoi.getPolygon(it)) }
            return Map(graph, voronoi.world)
        }
    }

}
