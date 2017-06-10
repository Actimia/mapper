package se.edstrompartners.mapper

import java.awt.Color


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