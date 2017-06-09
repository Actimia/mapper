package se.edstrompartners.mapper

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val size = 700
    val image = BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR)
    val g = image.createGraphics()
//    g.scale(0.9,0.9)
    g.color = Color.black
    g.fillRect(0, 0, image.width, image.width)

//    val rnd = Random(7812384712398)
    val rnd = Random()

    val world = World(size.toDouble(), size.toDouble())
    val numPoints = 400
    val distance = 25.0
//    val points = genPoints(10, size, margin, rnd)
    println("Generating points...")
    val points = genPointsUniform(numPoints, distance, world, rnd)
    g.color = Color.cyan
    points.forEach { g.drawPoint(it) }
    println("Calculating Delaney triangulation...")
    val delauney = DelauneyTriangulation.create(points, world)
//    drawDelauney(g, delauney, points)
//    val graph = delauney.toGraph()
//    g.color = Color(255, 0, 0, 128)
//    graph.edges.forEach { g.drawLine(it) }
//    graph.nodes.forEach { g.drawPoint(it) }

//    val voronoi = VoronoiDiagram.createFromDelauney(delauney)
    println("Converting to Voronoi diagram...")
    val voronoi = VoronoiDiagram.createFromDelauney2(delauney, world)
    println("Average distance is ${voronoi.averageDistance()}")
    g.color = Color(0, 255, 0, 128)
//    voronoi.nodes.forEach { g.drawPoint(it);}
    voronoi.nodes.forEach { g.fillPolygon(voronoi.getPolygon(it)) }
//    g.color = Color.red
    voronoi.edges.forEach { g.drawLine(it) }
//    g.color = Color.red

//    val node = voronoi.nodes.drop(2).first()
//    val poly = voronoi.getPolygon(node)
//    g.drawPolygon(poly)
//    for ((i, edge) in edges.withIndex()){
//        g.drawLine(edge)
//        g.drawString("$i", edge.a.x.toInt(), edge.a.y.toInt())
//    }

    ImageIO.write(image, "png", File("out.png"))
}

private fun drawDelauney(g: Graphics, delauney: DelauneyTriangulation, points: List<Vec>) {
    g.color = Color(255, 0, 0, 64)
    delauney.triangles.forEach {
        g.fillPolygon(
            it.vertices.map { it.x.toInt() }.toIntArray(),
            it.vertices.map { it.y.toInt() }.toIntArray(),
            3
        )
        it.edges().forEach { g.drawLine(it) }
    }

    g.color = Color.red
    points.forEach { g.drawPoint(it) }
}

fun genPoints(num: Int, size: Int, margin: Int = 0, rnd: Random = Random()): List<Vec> {
    return (0..num).map {
        Vec(margin + rnd.nextDouble() * (size - 2 * margin),
            margin + rnd.nextDouble() * (size - 2 * margin))
    }
}

fun genPointsUniform(num: Int, minDist: Double, world: World, rnd: Random = Random()): List<Vec> {
    val points = mutableListOf<Vec>()

    generateSequence {
        world.getRandomPoint(rnd)
    }.filter {
        candidate ->
        points.none { it.isWithin(candidate, minDist) }
    }.take(num).toCollection(points)

    return points
}