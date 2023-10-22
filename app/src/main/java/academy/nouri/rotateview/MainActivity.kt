package academy.nouri.rotateview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private val imageView by lazy { findViewById<ImageView>(R.id.image) }
    private val rotateView by lazy { RotateView.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Check support
        rotateView?.let {
            if (it.isDeviceSupported()) it.setImageWithDrawable(imageView, R.drawable.wallpaper)?.center()
        }
        //Register
        rotateView?.registerListener()
    }

    override fun onStop() {
        super.onStop()
        rotateView?.unRegisterListener()
    }
}