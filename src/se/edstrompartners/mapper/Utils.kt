package se.edstrompartners.mapper

import java.awt.Graphics
import java.util.concurrent.ThreadLocalRandom

fun <E> List<E>.getAny(): E {
    if (isEmpty())
        throw IllegalStateException("List can not be empty!")

    return get(ThreadLocalRandom.current().nextInt(0, size))
}

fun Graphics.drawPoint(p: Vec) {
    fillRect(p.x.toInt() - 1, p.y.toInt() - 1, 3, 3)
}

fun Graphics.drawLine(x: Vec, y: Vec) {
    drawLine(
        x.x.toInt(),
        x.y.toInt(),
        y.x.toInt(),
        y.y.toInt()
    )
}

fun Graphics.drawPolygon(p: Polygon) {
    drawPolygon(
        p.points.map { it.x.toInt() }.toIntArray(),
        p.points.map { it.y.toInt() }.toIntArray(),
        p.points.size
    )
}

fun Graphics.fillPolygon(p: Polygon) {
    fillPolygon(
        p.points.map { it.x.toInt() }.toIntArray(),
        p.points.map { it.y.toInt() }.toIntArray(),
        p.points.size
    )
}

fun ccw(a: Vec, b: Vec, c: Vec): Double {
    val (x1, y1) = a
    val (x2, y2) = b
    val (x3, y3) = c
    return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1)
}

fun Graphics.drawLine(l: Line) {
    drawLine(l.a, l.b)
}

fun Graphics.drawCircle(mid: Vec, radius: Double) {
    drawOval((mid.x - radius).toInt(), (mid.y - radius).toInt(), (radius * 2).toInt(), (radius * 2).toInt())
}