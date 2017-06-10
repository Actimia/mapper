package se.edstrompartners.mapper

import org.apache.commons.math3.random.JDKRandomGenerator
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val height = 1080
    val width = 1920
    val image = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
    val g = image.createGraphics()
//    g.scale(0.9,0.9)
    g.color = Color.black
    g.fillRect(0, 0, image.width, image.width)

//    val rnd = Random(7812384712398)
    val world = World(width.toDouble(), height.toDouble(), JDKRandomGenerator())
    val numPoints = 2000
    val distance = 25.0
//    val points = genPoints(10, size, margin, rnd)
    println("Generating points...")
    val points = genPointsUniform(numPoints, distance, world)
    g.color = Color.cyan
    points.forEach { g.drawPoint(it) }
    println("Calculating Delaney triangulation...")
    val delauney = DelauneyTriangulation.create(points, world)
    println("Converting to Voronoi diagram...")
    val voronoi = VoronoiDiagram.createFromDelauney2(delauney, world)
    println("Average distance is ${voronoi.averageDistance()}")
    println("Creating map...")
    val map = Map.create(voronoi)
    map.draw(g)
    println(map.graph.nodes.map { it.elevation }.max())
    println(map.graph.nodes.map { it.elevation }.min())

    ImageIO.write(image, "png", File("out.png"))
}


fun genPointsUniform(num: Int, minDist: Double, world: World): List<Vec> {
    val points = mutableListOf<Vec>()

    generateSequence {
        world.getRandomPoint()
    }.filter {
        candidate ->
        points.none { it.isWithin(candidate, minDist) }
    }.take(num).toCollection(points)

    return points
}