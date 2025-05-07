package com.yourpackage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.imageview.ShapeableImageView
import android.widget.ArrayAdapter
import com.example.sportapp.R
import com.example.sportapp.DiseaseItem
import com.example.sportapp.MainActivity8

class DiseaseAdapter(
    private val context: Context,
    private val diseaseList: MutableList<DiseaseItem>
) : ArrayAdapter<DiseaseItem>(context, R.layout.list_item, diseaseList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.itemText)
        val deleteButton = view.findViewById<ShapeableImageView>(R.id.listImage)

        val item = diseaseList[position]

        textView.text = item.name

        deleteButton.setOnClickListener {
            diseaseList.removeAt(position)
            notifyDataSetChanged()
            // Вызываем обновление состояния кнопок
            if (context is MainActivity8) {
                context.updateButtonState()
            }
        }

        return view
    }
}