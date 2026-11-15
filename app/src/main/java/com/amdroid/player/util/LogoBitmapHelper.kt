package com.amdroid.player.util

import android.graphics.*

object LogoBitmapHelper {

    private var cachedBitmap: Bitmap? = null

    fun getLogoBitmap(): Bitmap {
        cachedBitmap?.let { return it }

        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Dark Titanium Rounded Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0F131A")
            style = Paint.Style.FILL
        }
        val bgRect = RectF(8f, 8f, (size - 8).toFloat(), (size - 8).toFloat())
        canvas.drawRoundRect(bgRect, 48f, 48f, bgPaint)

        // Border Glow
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0284C7")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(bgRect, 48f, 48f, borderPaint)

        // 2. Letters E and B
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F8FAFC")
            textSize = 72f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            setShadowLayer(8f, 0f, 4f, Color.parseColor("#000000"))
        }

        // Draw 'E' on the left
        canvas.drawText("E", 60f, 150f, textPaint)

        // Draw 'B' on the right
        canvas.drawText("B", 196f, 150f, textPaint)

        // 3. Thin Electric Blue Lightning Bolt in the Center
        val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#38BDF8")
            style = Paint.Style.FILL
            setShadowLayer(16f, 0f, 0f, Color.parseColor("#00F0FF"))
        }

        val boltPath = Path().apply {
            moveTo(136f, 38f)
            lineTo(108f, 118f)
            lineTo(130f, 118f)
            lineTo(116f, 218f)
            lineTo(158f, 106f)
            lineTo(132f, 106f)
            close()
        }
        canvas.drawPath(boltPath, boltPaint)

        // Lightning stroke outline
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
        }
        canvas.drawPath(boltPath, strokePaint)

        // 4. Bottom Cyan Accent Bar
        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0284C7")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(60f, 226f, 196f, 234f), 4f, 4f, barPaint)

        cachedBitmap = bitmap
        return bitmap
    }
}
