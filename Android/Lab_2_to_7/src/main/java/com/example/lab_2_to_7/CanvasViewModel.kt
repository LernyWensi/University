import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream


class CanvasViewModel(context: Context?) : View(context) {
    private lateinit var path: Path
    private var paint: Paint? = null
    private var bitmapPaint: Paint? = null
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    init {
        bitmapPaint = Paint(Paint.DITHER_FLAG)
        paint = Paint()

        paint!!.isAntiAlias = true
        paint?.setColor(Color.GREEN)
        paint?.style = Paint.Style.STROKE

        paint?.strokeJoin = Paint.Join.ROUND
        paint?.strokeCap = Paint.Cap.ROUND
        paint?.strokeWidth = 12F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap!!, 0f, 0f, bitmapPaint!!)
    }

    private fun drawCircle() {
        canvas!!.drawCircle(100f, 100f, 50f, paint!!)
        invalidate()
    }

    private fun drawSquare() {
        canvas!!.drawRect(200f, 200f, 300f, 300f, paint!!)
        invalidate()
    }

    private fun drawCat() {
        val mBitmapFromSdcard = BitmapFactory.decodeFile("/sdcard/Pictures/another_cat_square.png")
        canvas!!.drawBitmap(mBitmapFromSdcard, 100f, 100f, paint)
        invalidate()
    }

    private fun onSaveClick() {
        val destPath: String = context.getExternalFilesDir(null)!!.absolutePath
        val file = File(destPath, "Image.PNG")

        val outStream = FileOutputStream(file)
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outStream)

        outStream.flush()
        outStream.close()
    }

    val funcArray = arrayOf(::drawSquare, ::drawCircle, ::drawCat, ::onSaveClick)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path = Path()
                path.moveTo(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> path.lineTo(event.x, event.y)
        }

        canvas!!.drawPath(path, paint!!)
        invalidate()

        return true
    }
}