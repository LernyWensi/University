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
        paint?.setColor(Color.MAGENTA)
        paint?.style = Paint.Style.STROKE

        paint?.strokeJoin = Paint.Join.ROUND
        paint?.strokeCap = Paint.Cap.ROUND
        paint?.strokeWidth = 8F
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
        canvas!!.drawCircle(700f, 300f, 90f, paint!!)
        invalidate()
    }

    private fun drawSquare() {
        canvas!!.drawRect(100f, 300f, 400f, 600f, paint!!)
        invalidate()
    }

    private fun drawCat() {
        val mBitmapFromSdcard = BitmapFactory.decodeFile("/sdcard/Pictures/another_cat_square.png")
        canvas!!.drawBitmap(mBitmapFromSdcard, 30f, 800f, paint)
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

    val funcArray = arrayOf(::drawSquare, ::drawCircle, ::drawCat, ::drawSecondName, ::onSaveClick)

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

    private fun drawDiagonalLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        canvas!!.drawLine(startX, startY, endX, endY, paint!!)
    }

    private fun drawSecondName() {
        drawDiagonalLine(50f, 50f, 50f, 150f)
        drawDiagonalLine(50f, 50f, 120f, 50f)
        drawDiagonalLine(120f, 50f, 120f, 150f)

        canvas!!.drawOval(150f, 50f, 220f, 150f,
            Paint().apply {
                strokeWidth = 8F
                style = Paint.Style.STROKE
                color = Color.MAGENTA
            }
        )

        drawDiagonalLine(250f, 50f, 320f, 150f)
        drawDiagonalLine(320f, 50f, 250f, 150f)

        drawDiagonalLine(350f, 50f, 350f, 150f)
        drawDiagonalLine(420f, 50f, 350f, 150f)
        drawDiagonalLine(420f, 50f, 420f, 150f)

        drawDiagonalLine(450f, 150f, 485f, 50f)
        drawDiagonalLine(520f, 150f, 485f, 50f)

        drawDiagonalLine(550f, 50f, 550f, 150f)
        drawDiagonalLine(550f, 150f, 620f, 150f)
        drawDiagonalLine(550f, 95f, 620f, 95f)
        drawDiagonalLine(620f, 95f, 620f, 150f)

        drawDiagonalLine(650f, 50f, 650f, 150f)
        drawDiagonalLine(650f, 100f, 705f, 50f)
        drawDiagonalLine(650f, 100f, 705f, 150f)

        canvas!!.drawOval(735f, 50f, 805f, 150f,
            Paint().apply {
                strokeWidth = 8F
                style = Paint.Style.STROKE
                color = Color.MAGENTA
            }
        )
    }
}