package com.example.lab5

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import android.content.Intent
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider


class MainActivity2 : AppCompatActivity() {

    private fun buildList(): ArrayList<PdfFile>{
        val list = ArrayList<PdfFile>()

        //проверяем наличие файлов в папке, собираем файлы в список
        val file = File("/storage/self/primary/Download/lab5")
        val listFiles = file.listFiles()
        for (f: File in listFiles!!){
            if (f.name.endsWith("pdf")){ // pdf
                list.add(PdfFile(f.name, f.absolutePath, (f.length()/1024/1024).toString()))
                Log.d("MyLog", f.name)
            }
        }
        return list
    }

    private fun buildRecyclerView(mode: String?, list: ArrayList<PdfFile>): RecyclerView{
        val recyclerview: RecyclerView = findViewById(R.id.rv)
        recyclerview.adapter = CustomRecyclerAdapter(list, mode!!)
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
            val list = buildList()
            val mode = intent.getStringExtra("key")
            buildRecyclerView(mode!!, list)

        } catch (e: Exception) { Log.d("MyLog", "$e") }
    }
}

// адаптер
class CustomRecyclerAdapter(private val names: ArrayList<PdfFile>, var mode: String)
    : RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txtFileName)
        val path: TextView = itemView.findViewById(R.id.txtFilePath)
        var size: TextView = itemView.findViewById(R.id.txtFileSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = names[position].name
        holder.path.text = names[position].path
        holder.size.text = "${names[position].size}Mb"

        if (mode == "open"){
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
        } else if (mode == "delete"){
            holder.itemView.setOnClickListener{
                try {
                    val f = File(it.findViewById<TextView>(R.id.txtFilePath).text as String)
                    f.delete()
                    notifyItemRemoved(position)
                } catch (e: Exception) { Log.d("MyLog", "$e") }
            }
        }
    }

    override fun getItemCount() = names.size
}

//класс для хранения .pdf
data class PdfFile(val name: String, val path: String, val size: String)
