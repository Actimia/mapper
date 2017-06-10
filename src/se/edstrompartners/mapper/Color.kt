package se.edstrompartners.mapper

import java.awt.Color
import java.awt.color.ColorSpace


class Colour private constructor(
    private val r: Int,
    private val g: Int,
    private val b: Int,
    private val a: Int = cmax) {

    init {
        val range = 0..255
        if (r !in range) throw IllegalArgumentException("Red component out of range: $r")
        if (g !in range) throw IllegalArgumentException("Blue component out of range: $g")
        if (b !in range) throw IllegalArgumentException("Green component out of range: $b")
        if (a !in range) throw IllegalArgumentException("Alpha component out of range: $a")
    }

    fun toColor(): Color {
        return Color(r, g, b, a)
    }


    companion object {
        val cmax = 255

        fun rgb(r: Int, g: Int, b: Int, a: Int = 255): Colour {
            return Colour(r, g, b, a)
        }

        fun rgb(r: Double, g: Double, b: Double, a: Double = 1.0): Colour {
            return rgb((r*cmax).toInt(), (g*cmax).toInt(), (b*cmax).toInt(), (a*cmax).toInt())
        }

        fun hsv(h: Double, s: Double, v: Double, a: Double = 1.0): Colour {
            val c = s * v
            val hprime = h * 6
            val x = c * (1 - Math.abs((hprime % 2) - 1))
            val cand = if (hprime < 1) {
                listOf(c, x, 0.0)
            } else if (hprime < 2) {
                listOf(x, c, 0.0)
            } else if (hprime < 3) {
                listOf(0.0, c, x)
            } else if (hprime < 4) {
                listOf(0.0, x, c)
            } else if (hprime < 5) {
                listOf(x, 0.0, c)
            } else {
                listOf(c, 0.0, x)
            }
            val m = v - c

            return rgb(cand[0] + m, cand[1] + m, cand[2] + m, a)
        }
    }
}

