package com.example.navapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CyberSecurityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cyber_security)

        // Programmatically insert the PDF into the database when the app starts
        val pdfAssetName = "cybersecurity.pdf"  // Updated file name for Cybersecurity PDF
        val pdfName = "cybersecurity_pdf"  // Name to identify the PDF in the database
        insertPdfFromAssets(pdfAssetName, pdfName)

        // Set up click listener for PDF link
        val pdfLinkTextView = findViewById<TextView>(R.id.text_pdf_link)
        pdfLinkTextView.setOnClickListener {
            // Retrieve the PDF from the database and open it
            val pdfBytes = getPdfFromDatabase(pdfName) // Retrieve PDF with the correct name

            if (pdfBytes != null) {
                val tempPdfFile = createTempPdfFile(pdfBytes)
                if (tempPdfFile != null) { // Ensure the file is not null before attempting to open
                    openPdf(tempPdfFile)
                } else {
                    // Handle the error where the file could not be created
                }
            } else {
                // Handle the case where the PDF was not found in the database
            }
        }
    }

    // Function to insert PDF into the database from assets
    private fun insertPdfFromAssets(pdfAssetName: String, pdfName: String) {
        try {
            // Open the PDF file from assets folder
            val inputStream = assets.open(pdfAssetName)
            val pdfBytes = inputStream.readBytes()  // Convert the file into byte array
            val dbHelper = PdfDatabaseHelper(this)
            dbHelper.insertPdf(pdfName, pdfBytes)  // Insert PDF into the database
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()  // Handle exception if the file is not found in assets
        }
    }

    // Function to retrieve the PDF from the database
    private fun getPdfFromDatabase(pdfName: String): ByteArray? {
        val dbHelper = PdfDatabaseHelper(this)
        return dbHelper.getPdf(pdfName) // Get PDF as byte array from the database
    }

    // Function to create a temporary file for the PDF from byte array
    private fun createTempPdfFile(pdfBytes: ByteArray): File? {
        return try {
            // Create a temporary file to store the PDF
            val tempFile = File.createTempFile("cybersecurity_", ".pdf", cacheDir)
            val fos = FileOutputStream(tempFile)
            fos.write(pdfBytes)  // Write byte array to the file
            fos.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()  // Handle exceptions
            null // Return null if an error occurs
        }
    }

    // Function to open the PDF using an external viewer
    fun openPdf(pdfFile: File) {
        val pdfUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Grant read permission to the external app
        }

        // Start the activity to view the PDF
        startActivity(intent)
    }
}
