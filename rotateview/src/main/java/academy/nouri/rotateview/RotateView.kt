package academy.nouri.rotateview

import academy.nouri.rotateview.Utils.getDeviceHeight
import academy.nouri.rotateview.Utils.smooth
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView

class RotateView private constructor(private val mContext: Context) : SensorEventListener {
    private var smoothedValue = 0f
    private lateinit var imageView: ImageView
    private var mImageWidth = 0
    private var mMaxScroll = 0

    init {
        mSensorManager = mContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setImageWithDrawable(image: ImageView, drawable: Int): RotateView? {
        imageView = image
        val bmp = resizeBitmap(getDeviceHeight(mContext), drawable)
        imageView.layoutParams = FrameLayout.LayoutParams(bmp.width, bmp.height)
        imageView.setImageBitmap(bmp)
        mMaxScroll = bmp.width
        if (image.parent is HorizontalScrollView) {
            (image.parent as HorizontalScrollView).setOnTouchListener { _, _ -> true }
        }
        return rotateView
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setImageWithBitmap(image: ImageView, bitmap: Bitmap): RotateView? {
        imageView = image
        val bmp = resizeBitmap(getDeviceHeight(mContext), bitmap)
        imageView.layoutParams = FrameLayout.LayoutParams(bmp.width, bmp.height)
        imageView.setImageBitmap(bmp)
        mMaxScroll = bmp.width
        if (image.parent is HorizontalScrollView) {
            (image.parent as HorizontalScrollView).setOnTouchListener { _, _ -> true }
        }
        return rotateView
    }

    private fun resizeBitmap(targetH: Int, drawable: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(mContext.resources, drawable)
        mImageWidth = bitmap.width * getDeviceHeight(mContext) / bitmap.height
        return Bitmap.createScaledBitmap(bitmap, mImageWidth, targetH, true)
    }

    private fun resizeBitmap(targetH: Int, bitmap: Bitmap): Bitmap {
        mImageWidth = bitmap.width * getDeviceHeight(mContext) / bitmap.height
        return Bitmap.createScaledBitmap(bitmap, mImageWidth, targetH, true)
    }

    fun center(): RotateView? {
        imageView.post {
            if (mImageWidth > 0) {
                (imageView.parent as HorizontalScrollView).scrollX = mImageWidth / 4
            } else {
                (imageView.parent as HorizontalScrollView).scrollX = (imageView.parent as HorizontalScrollView).width / 2
            }
        }
        return rotateView
    }

    fun isDeviceSupported(): Boolean {
        return null != mSensorManager && null != mSensor
    }

    fun registerListener() {
        if (isDeviceSupported()) mSensorManager!!.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun unRegisterListener() {
        if (isDeviceSupported()) mSensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        var value = event.values[1]
        value *= 50
        smoothedValue = smooth(value, smoothedValue)
        value = smoothedValue
        val scrollX = (imageView.parent as HorizontalScrollView).scrollX
        if (scrollX + value >= mMaxScroll) value = (mMaxScroll - scrollX).toFloat()
        if (scrollX + value <= -mMaxScroll) value = (-mMaxScroll - scrollX).toFloat()
        (imageView.parent as HorizontalScrollView).scrollBy(value.toInt(), 0)
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var rotateView: RotateView? = null
        private var mSensorManager: SensorManager? = null
        private var mSensor: Sensor? = null

        @Synchronized
        fun getInstance(context: Context): RotateView? {
            if (rotateView == null) {
                rotateView = RotateView(context.applicationContext)
            }
            return rotateView
        }
    }
}