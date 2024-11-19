package com.example.navapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PdfDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pdfDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "pdfs"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PART_INDEX = "part_index"
        private const val COLUMN_PDF_PART = "pdf_part" // Store PDF in parts
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_PART_INDEX INTEGER,
                $COLUMN_PDF_PART BLOB
            )
        """
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert PDF into the database in parts
    fun insertPdf(name: String, pdf: ByteArray): Long {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Chunk size (e.g., 1MB per chunk)
            val chunkSize = 1024 * 1024 // 1MB
            val totalChunks = Math.ceil(pdf.size / chunkSize.toDouble()).toInt()

            for (i in 0 until totalChunks) {
                val start = i * chunkSize
                val end = minOf(start + chunkSize, pdf.size)
                val chunk = pdf.copyOfRange(start, end)

                val values = ContentValues()
                values.put(COLUMN_NAME, name)
                values.put(COLUMN_PART_INDEX, i)
                values.put(COLUMN_PDF_PART, chunk)

                db.insert(TABLE_NAME, null, values)
            }
            db.setTransactionSuccessful()
            return 1 // Success
        } catch (e: Exception) {
            e.printStackTrace()
            return -1 // Failure
        } finally {
            db.endTransaction()
        }
    }

    // Retrieve PDF from the database by name
    @SuppressLint("Range")
    fun getPdf(name: String): ByteArray? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_PDF_PART),
            "$COLUMN_NAME = ?",
            arrayOf(name),
            null,
            null,
            "$COLUMN_PART_INDEX ASC" // Ensure parts are ordered
        )

        val pdfParts = mutableListOf<ByteArray>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val part = cursor.getBlob(cursor.getColumnIndex(COLUMN_PDF_PART))
                pdfParts.add(part)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Combine all parts into a single byte array
        return if (pdfParts.isNotEmpty()) {
            pdfParts.reduce { acc, bytes -> acc + bytes }
        } else {
            null
        }
    }
}
