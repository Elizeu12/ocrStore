package com.belcompany.compras

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belcompany.compras.data.Element
import java.util.ArrayList

class RecyclerListFragment : Fragment(R.layout.recycler_list_fragment) {

    private lateinit var arrayList: ArrayList<Element>
    private lateinit var textView: TextView
    private var total: Double = 0.0
    private lateinit var recycler: RecyclerView
    private val model: ViewModelStore by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recycle_view)
        textView = view.findViewById(R.id.txt_total)

        arrayList = arrayListOf()
        recycler.adapter = Adapter(arrayList)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)

        model.myObject.observe (viewLifecycleOwner){

            total += it.price
            textView.text = "R$$total"

            arrayList = arrayListOf()
            arrayList.add(it)

            textView.text = total.toString().replace('.', ',')
            recycler.adapter?.notifyDataSetChanged()
        }
    }
}