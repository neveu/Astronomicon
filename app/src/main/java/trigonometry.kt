import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

fun sind(degrees: Double): Double {
    return sin(degrees * Math.PI / 180.0)
}

fun sind(degrees: Float): Float {
    return sin(degrees * Math.PI / 180.0).toFloat()
}


fun cosd(degrees: Double): Double {
    return cos(degrees * Math.PI / 180.0)
}

fun cosd(degrees: Float): Float {
    return cos(degrees * Math.PI / 180.0).toFloat()
}

fun tand(degrees: Double): Double {
    return tan(degrees * Math.PI / 180.0)
}

fun atand(tan: Double): Double {
    return atan(tan) * Math.PI / 180.0
}

fun asind(sin: Double): Double {
    return asin(sin) * Math.PI / 180.0
}
fun angle(x: Double, y: Double): Double {
    return  atan2(y, x)
}

fun angled(x: Double, y: Double): Double {
    return atan2(y, x) * 180.0 / Math.PI
}

fun elevationd(x: Double, y: Double, z: Double): Double {
    // [x y z] . [x y 0]/|[x y 0]||[x y z]|
    return (180.0/Math.PI) * atan2(z, sqrt(x*x + y*y))


}

fun r(x: Double, y: Double): Double {
    return sqrt(x * x + y * y)
}
