package com.belcompany.compras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.belcompany.compras.data.Element

class Adapter(private val dataSet: ArrayList<Element>):RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val textPriceView: TextView

        init {
            textView = view.findViewById(R.id.text_item)
            textPriceView = view.findViewById(R.id.text_value)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_list, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView.text = dataSet[position].title
        val price = dataSet[position].price.toString().replace('.', ',')
        viewHolder.textPriceView.text =price
    }

    override fun getItemCount() = dataSet.size

}