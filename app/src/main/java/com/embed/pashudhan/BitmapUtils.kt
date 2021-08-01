package com.embed.pashudhan

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException
import java.io.InputStream

class BitmapUtils {

    fun getBitmapFromAssets(context: Context, fileName: String, width: Int, height: Int): Bitmap? {
        var assetManager: AssetManager = context.assets

        var inputStream: InputStream
        var bitmap: Bitmap
        try {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = assetManager.open(fileName)
            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getBitmapFromGallery(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
        var filePathColumn: Array<String> = (MediaStore.Images.Media.DATA) as Array<String>
        var cursor: Cursor? = context.contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        var columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        var picturePath = cursor?.getString(columnIndex!!)
        cursor?.close()

        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(picturePath, options)
    }

    fun applyOverlay(
        context: Context,
        sourceImage: Bitmap,
        overlayDrawableResourceId: Int
    ): Bitmap? =
        try {
            val width = sourceImage.width
            val height = sourceImage.height
            val resources = context.resources

            val imageAsDrawable = BitmapDrawable(resources, sourceImage)
            val layers = arrayOfNulls<Drawable>(2)

            layers[0] = imageAsDrawable
            layers[1] = BitmapDrawable(
                resources,
                decodeSampledBitmapFromResource(
                    resources,
                    overlayDrawableResourceId,
                    width,
                    height
                )
            )
            val layerDrawable = LayerDrawable(layers)
            drawableToBitmap(layerDrawable)
        } catch (ex: Exception) {
            null
        }

    fun decodeSampledBitmapFromResource(
        res: Resources, resId: Int,
        reqWidth: Int, reqHeight: Int
    ): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}