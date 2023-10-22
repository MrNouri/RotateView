package academy.nouri.rotateview

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.view.Display

internal object Utils {
    fun smooth(input: Float, output: Float): Float {
        val alpha = 0.2f
        return (output + alpha * (input - output)).toInt().toFloat()
    }

    fun getDeviceHeight(context: Context): Int {
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val size = Point()
        @Suppress("DEPRECATION")
        display.getSize(size)
        return size.y
    }
}