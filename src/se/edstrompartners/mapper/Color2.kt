package se.edstrompartners.mapper

import java.awt.Color
import java.lang.Math.*


data class HSV(
    val h: Double,
    val s: Double,
    val v: Double,
    val a: Double = 1.0
) {
    fun color(): Color {
        fun scale(x: Double) : Int = (x*255).toInt()
        if (s == 0.0) { // no saturation -> greyscale image -> hue does not matter
            val grey = scale(v)
            return Color(grey, grey, grey, scale(a))
        }

        val hprime = h * 6 % 6

        val hdiff = hprime - Math.floor(hprime)

        val p1 = v * (1 - s)
        val p2 = v * (1 - s * (hdiff))
        val p3 = v * (1 - s * (1 - hdiff))

        val (r, g, b) = if (hprime < 1) {
            Triple(v, p3, p1)
        } else if (hprime < 2) {
            Triple(p2, v, p1)
        } else if (hprime < 3) {
            Triple(p1, v, p3)
        } else if (hprime < 4) {
            Triple(p1, p2, v)
        } else if (hprime < 5) {
            Triple(p3, p1, v)
        } else {
            Triple(v, p1, p2)
        }

        return Color(scale(r), scale(g), scale(b), scale(a))
    }
}

class Gradient(private val c1: HSV, private val c2 : HSV) {
    fun get(f: Double) : HSV {
        val c1h = Vec(sin(c1.h), cos(c1.h))
        val c2h = Vec(sin(c2.h), cos(c2.h))
        val hAngle = lerp(f, c1h, c2h).theta()
        val h = (hAngle + PI) / (2*PI)
//        val h = lerp(f, c1.h, c2.h)
        val s = lerp(f, c1.s, c2.s)
        val v = lerp(f, c1.v, c2.v)
        val a = lerp(f, c1.a, c2.a)



        return HSV(h,s,v,a)
    }
}