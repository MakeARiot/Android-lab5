package com.example.lab5

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.internal.ContextUtils.getActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import java.io.File
import java.io.FileOutputStream
import android.net.Uri

import android.provider.Settings
import androidx.core.app.ActivityCompat
import java.security.AccessController.getContext
import kotlin.math.absoluteValue


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent1 = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent1.addCategory("android.intent.category.DEFAULT")
        intent1.data =
            Uri.parse(java.lang.String.format("package:%s", getActivity(this)?.packageName))
        getActivity(this)?.startActivityForResult(intent1, 200)

        try {
            val btn1: Button = findViewById(R.id.button)

            //клиент для подключения
            val client = AsyncHttpClient()
            client.setURLEncodingEnabled(false)
            //для файлов ... на сервере
            btn1.setOnClickListener {

                Toast.makeText(applicationContext,
                    "Подожди минутку...", Toast.LENGTH_SHORT)
                    .show()
                for (id in 1..10){
                    client["http://ntv.ifmo.ru/file/journal/${id}.pdf", RequestParams(), object: AsyncHttpResponseHandler(){
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            responseBody: ByteArray?
                        ) {
                            //вывожу статус подключения в консоль
                            Log.d("MyLog", "statusCode: $statusCode")
                            try {
                                //создал папку
                                File("/storage/self/primary/Download/lab5/").mkdir()
                                //записываю .pdf в созданную папку
                                val fos = FileOutputStream("/storage/self/primary/Download/lab5/$id.pdf")
                                fos.write(responseBody)
                                Log.d("MyLog", "saved in ${File(getExternalFilesDir(null), "$id.pdf").absolutePath}")
                                fos.close()
                                //вызвожу путь до файла в тосте
                                Toast.makeText(applicationContext,
                                    "saved in ${File(getExternalFilesDir(null), "$id.pdf").absolutePath}", Toast.LENGTH_SHORT)
                                    .show()
                            } catch (e:Exception){ Log.d("MyLog", "${e.stackTrace}") }

                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<out Header>?,
                            responseBody: ByteArray?,
                            error: Throwable?
                        ) {
                            Log.d("MyLog", "почему-то не подключился")
                            Log.d("MyLog", error.toString())
                        }
                    }]
                }
            }

        } catch (e: Exception) { Log.d("MyLog", "$e") }
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()

            try {
                val btn2: Button = findViewById(R.id.button2)
                val btn3: Button = findViewById(R.id.button3)
                val intent = Intent(this, MainActivity2::class.java)

                btn2.setOnClickListener {
                    intent.putExtra("key", "open")
                    startActivity(intent)
                }

                btn3.setOnClickListener {
                    intent.putExtra("key", "delete")
                    startActivity(intent)
                }

            } catch (e: Exception) { Log.d("MyLog", "$e") }
    }
}
