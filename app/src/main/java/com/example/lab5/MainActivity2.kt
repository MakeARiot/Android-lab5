package com.example.lab5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider


class MainActivity2 : AppCompatActivity() {

    private fun buildRecyclerView(mode: String?): RecyclerView{
        val recyclerview: RecyclerView = findViewById(R.id.rv)
        val list = ArrayList<PdfFile>()

        //проверяем наличие файлов в папке, собираем файлы в список
        val file = File("/storage/self/primary/Download/lab5")
        val listFiles = file.listFiles()
        for (f: File in listFiles){
            if (f.name.endsWith("pdf")){ // pdf
               list.add(PdfFile(f.name, f.absolutePath))
                Log.d("MyLog", f.name + "from if")
            }
        }

        if (mode == "delete"){
            //передаем список файлов адаптеру, ставим адаптер в RecyclerView
            recyclerview.adapter = CustomRecyclerAdapterDelete(list)
        } else if (mode == "open"){
            recyclerview.adapter = CustomRecyclerAdapterShow(list)
        }
        recyclerview.layoutManager = LinearLayoutManager(this)

        return recyclerview
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()

        try {
            val mode = intent.getStringExtra("key")
            Log.d("MyLog", mode!!)
            buildRecyclerView(mode!!)

        } catch (e: Exception) { Log.d("MyLog", "$e") }
    }
}

//мой адаптер на просмотр
class CustomRecyclerAdapterShow(private val names: ArrayList<PdfFile>)
    : RecyclerView.Adapter<CustomRecyclerAdapterShow.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txtFileName)
        val path: TextView = itemView.findViewById(R.id.txtFilePath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = names[position].name
        holder.path.text = names[position].path

        //обработчик нажатий на запись
        holder.itemView.setOnClickListener{
            try {
                val f = File(it.findViewById<TextView>(R.id.txtFilePath).text as String)
                val uri = FileProvider.getUriForFile(holder.itemView.context, BuildConfig.APPLICATION_ID + ".provider", f)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "application/pdf"
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it.context, intent, null)
            } catch (e: Exception) { Log.d("MyLog", "$e") }
        }
    }

    override fun getItemCount() = names.size
}

//мой адаптер на удаление
class CustomRecyclerAdapterDelete(private val names: ArrayList<PdfFile>)
    : RecyclerView.Adapter<CustomRecyclerAdapterDelete.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txtFileName)
        val path: TextView = itemView.findViewById(R.id.txtFilePath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = names[position].name
        holder.path.text = names[position].path

        //обработчик нажатий на запись
        holder.itemView.setOnClickListener{
            try {
                val f = File(it.findViewById<TextView>(R.id.txtFilePath).text as String)
                f.delete()
            } catch (e: Exception) { Log.d("MyLog", "$e") }
        }
    }

    override fun getItemCount(): Int = names.size
}

//класс для хранения .pdf
data class PdfFile(val name: String, val path: String)
