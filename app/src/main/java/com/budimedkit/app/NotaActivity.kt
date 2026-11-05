package com.budimedkit.app
import android.app.Activity
import android.os.Bundle
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.view.Gravity
import android.webkit.WebView
import android.widget.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotaActivity : Activity() {
 private lateinit var db: DatabaseHelper
 private lateinit var nama: EditText
 private lateinit var hp: EditText
 private lateinit var kendaraan: EditText
 private lateinit var nopol: EditText
 private lateinit var pekerjaan: EditText
 private lateinit var biaya: EditText
 private val merah = Color.rgb(190,18,28)

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  db = DatabaseHelper(this)
  val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(246,247,250)) }
  root.addView(TextView(this).apply {
   text="‹   CETAK NOTA SERVIS"; textSize=20f; setTextColor(Color.WHITE); setTypeface(null,Typeface.BOLD)
   setPadding(dp(16),dp(18),dp(12),dp(18)); setBackgroundColor(merah); setOnClickListener { finish() }
  })
  val scroll=ScrollView(this)
  val form=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(18),dp(16),dp(18),dp(24)) }
  form.addView(TextView(this).apply { text="Data pelanggan dan servis"; textSize=19f; setTextColor(Color.DKGRAY); setTypeface(null,Typeface.BOLD) })
  nama=field(form,"Nama pelanggan"); hp=field(form,"Nomor HP pelanggan")
  kendaraan=field(form,"Merek / tipe kendaraan"); nopol=field(form,"Nomor polisi")
  pekerjaan=field(form,"Keluhan / pekerjaan servis"); biaya=field(form,"Total biaya (Rp)",true)
  form.addView(TextView(this).apply {
   text="SIMPAN & CETAK NOTA"; gravity=Gravity.CENTER; textSize=15f; setTextColor(Color.WHITE)
   setTypeface(null,Typeface.BOLD); setPadding(0,dp(17),0,dp(17))
   background=GradientDrawable().apply { setColor(merah); cornerRadius=dp(13).toFloat() }
   isClickable=true; setOnClickListener { saveAndPrint() }
  },LinearLayout.LayoutParams(-1,dp(54)).apply { topMargin=dp(18) })
  form.addView(TextView(this).apply { text="Nota akan dicatat sebagai servis selesai dan pemasukan keuangan."; textSize=12f; setTextColor(Color.GRAY); setPadding(0,dp(10),0,0) })
  scroll.addView(form); root.addView(scroll); setContentView(root)
 }
 private fun field(parent:LinearLayout,hintText:String,numeric:Boolean=false):EditText {
  parent.addView(TextView(this).apply { text=hintText; textSize=13f; setTextColor(Color.DKGRAY); setTypeface(null,Typeface.BOLD); setPadding(0,dp(12),0,dp(5)) })
  val e=EditText(this).apply { hint=hintText; textSize=14f; setPadding(dp(12),dp(6),dp(12),dp(6)); background=GradientDrawable().apply { setColor(Color.WHITE); cornerRadius=dp(10).toFloat() }; if(numeric) inputType=android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
  parent.addView(e,LinearLayout.LayoutParams(-1,dp(50))); return e
 }
 private fun saveAndPrint() {
  val customer=nama.text.toString().trim(); val phone=hp.text.toString().trim()
  val vehicle=kendaraan.text.toString().trim(); val plate=nopol.text.toString().trim()
  val work=pekerjaan.text.toString().trim(); val amount=biaya.text.toString().trim().toDoubleOrNull()
  if(customer.isBlank()||vehicle.isBlank()||plate.isBlank()||work.isBlank()||amount==null||amount<=0) { Toast.makeText(this,"Lengkapi data pelanggan, kendaraan, pekerjaan, dan biaya",Toast.LENGTH_LONG).show(); return }
  val date=SimpleDateFormat("yyyy-MM-dd",Locale("id","ID")).format(Date())
  val values=ContentValues().apply { put("tanggal",date); put("pelanggan",customer); put("kendaraan",vehicle+" - "+plate); put("keluhan",work); put("biaya",amount); put("status","Selesai") }
  val id=db.writableDatabase.insert("servis",null,values)
  if(id<0) { Toast.makeText(this,"Gagal menyimpan nota",Toast.LENGTH_LONG).show(); return }
  if(!db.tambahTransaksi(date,"PEMASUKAN","Nota servis #"+id+" - "+customer+" - "+plate,amount)) {
   db.writableDatabase.delete("servis","id=?",arrayOf(id.toString())); Toast.makeText(this,"Gagal mencatat pemasukan",Toast.LENGTH_LONG).show(); return
  }
  printNota(id,date,customer,phone,vehicle,plate,work,amount)
 }
 private fun printNota(id:Long,date:String,customer:String,phone:String,vehicle:String,plate:String,work:String,amount:Double) {
  val bytes=ByteArrayOutputStream(); resources.openRawResource(R.drawable.budi_jaya_motor_logo).use { it.copyTo(bytes) }
  val logo=Base64.encodeToString(bytes.toByteArray(),Base64.NO_WRAP)
  val total="Rp "+String.format("%,.0f",amount).replace(',', '.')
  fun esc(v:String)=v.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
  val template="""<html><head><meta name="viewport" content="width=device-width"><style>
  body{font-family:Arial;margin:12px;color:#222;font-size:12px}.paper{max-width:340px;margin:auto}
  .logo{text-align:center}.logo img{width:210px;max-height:100px;object-fit:contain}h2{text-align:center;color:#b91220;margin:4px}
  .center{text-align:center;line-height:1.5}hr{border:0;border-top:1px dashed #333;margin:10px 0}
  table{width:100%}td{padding:4px 0;vertical-align:top}.total{font-size:16px;font-weight:bold;border-top:1px solid #333}
  .thanks{text-align:center;margin-top:18px}</style></head><body><div class="paper">
  <div class="logo"><img src="data:image/jpeg;base64,@@LOGO@@"></div><h2>BUDI JAYA MOTOR</h2>
  <div class="center">Jl. Warung Jaud Link Sumursana<br>Kec. Serang, Banten<br>HP. 087809927449</div><hr>
  <table><tr><td>No. Nota</td><td>: BJM-@@ID@@</td></tr><tr><td>Tanggal</td><td>: @@DATE@@</td></tr>
  <tr><td>Pelanggan</td><td>: @@CUSTOMER@@</td></tr><tr><td>No. HP</td><td>: @@PHONE@@</td></tr>
  <tr><td>Kendaraan</td><td>: @@VEHICLE@@</td></tr><tr><td>No. Polisi</td><td>: @@PLATE@@</td></tr></table>
  <hr><b>Keluhan / Pekerjaan:</b><br>@@WORK@@<hr>
  <table><tr><td class="total">TOTAL</td><td class="total" style="text-align:right">@@TOTAL@@</td></tr></table>
  <div class="thanks"><b>Terima kasih</b><br>Terpercaya • Berkualitas • Bergaransi</div></div></body></html>"""
  val html=template.replace("@@LOGO@@",logo).replace("@@ID@@",id.toString()).replace("@@DATE@@",esc(date))
   .replace("@@CUSTOMER@@",esc(customer)).replace("@@PHONE@@",esc(if(phone.isBlank()) "-" else phone))
   .replace("@@VEHICLE@@",esc(vehicle)).replace("@@PLATE@@",esc(plate)).replace("@@WORK@@",esc(work)).replace("@@TOTAL@@",total)
  val web=WebView(this).apply { settings.javaScriptEnabled=false; loadDataWithBaseURL(null,html,"text/html","UTF-8",null) }
  val manager=getSystemService(PRINT_SERVICE) as PrintManager
  manager.print("Nota Budi Jaya Motor",web.createPrintDocumentAdapter("Nota-BJM"),PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A5).setMinMargins(PrintAttributes.Margins.NO_MARGINS).build())
  Toast.makeText(this,"Nota tersimpan. Pilih printer untuk mencetak.",Toast.LENGTH_LONG).show()
 }
 private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}