package com.budimedkit.app

import android.app.Activity
import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null
    private val fileRequest = 4102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true
        web.addJavascriptInterface(PrintBridge(), "AndroidPrint")
        web.webViewClient = WebViewClient()
        web.webChromeClient = object : WebChromeClient() {
            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: android.webkit.JsPromptResult?): Boolean {
                val input = EditText(this@MainActivity).apply {
                    setText(defaultValue.orEmpty())
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    hint = message.orEmpty()
                    setSelection(text.length)
                }
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Ubah kategori")
                    .setView(input)
                    .setPositiveButton("Simpan") { _, _ -> result?.confirm(input.text.toString()) }
                    .setNegativeButton("Batal") { _, _ -> result?.cancel() }
                    .setOnCancelListener { result?.cancel() }
                    .show()
                return true
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: android.webkit.JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(message.orEmpty())
                    .setPositiveButton("OK") { _, _ -> result?.confirm() }
                    .setOnCancelListener { result?.cancel() }
                    .show()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: android.webkit.JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(message.orEmpty())
                    .setPositiveButton("Ya") { _, _ -> result?.confirm() }
                    .setNegativeButton("Batal") { _, _ -> result?.cancel() }
                    .setOnCancelListener { result?.cancel() }
                    .show()
                return true
            }

            override fun onShowFileChooser(view: WebView?, callback: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                fileCallback?.onReceiveValue(null)
                fileCallback = callback
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                startActivityForResult(Intent.createChooser(intent, "Pilih backup BUDI MEDKIT"), fileRequest)
                return true
            }
        }
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class PrintBridge {
        @JavascriptInterface
        fun printPage() {
            runOnUiThread {
                try {
                    val manager = getSystemService(PRINT_SERVICE) as PrintManager
                    manager.print("Nota Budi Jaya Motor", web.createPrintDocumentAdapter("Nota-Budi-Jaya-Motor"),
                        PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A5)
                            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build())
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Tidak dapat membuka layanan cetak: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileRequest) {
            val result = if (resultCode == RESULT_OK && data?.data != null) arrayOf(data.data!!) else null
            fileCallback?.onReceiveValue(result)
            fileCallback = null
        }
    }

    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
