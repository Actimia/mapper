package se.edstrompartners.mapper

import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.random.RandomGenerator
import java.util.*

class World(val width: Double, val height: Double, val random : RandomGenerator) {
    val bounds = Rect(0.0, 0.0, width, height)
    val dist = NormalDistribution(random, 0.5*width, 0.7*width)
//    private val dist = NormalDistribution(random, random.nextGaussian())
    private val noise = OpenSimplexNoise(random.nextLong())

    fun noise(p : Vec) : Double = noise(p.x, p.y)
    fun noise(x: Double, y : Double) : Double = noise.eval(x/width,y/height)

    fun closestIntersectionWithEdge(line: Line): Vec {
        return bounds.edges.map { it.intersection(line) }
            .filterNotNull()
            .minBy { (line.a - it).len() }!!
    }

    fun getRandomPoint(): Vec {
        return Vec(bounds.width * random.nextDouble(), bounds.height * random.nextDouble())
    }

    fun normal(p: Vec): Double {
        val d = p.dist(Vec(width/2, height/2))

        return 1 - dist.cumulativeProbability(d)
    }

}