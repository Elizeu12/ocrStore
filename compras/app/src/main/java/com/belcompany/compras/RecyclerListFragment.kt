package com.belcompany.compras

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belcompany.compras.data.Element
import java.util.ArrayList

class RecyclerListFragment : Fragment(R.layout.recycler_list_fragment) {

    private lateinit var arrayList: ArrayList<Element>
    private lateinit var textView: TextView
    private var total: Double = 0.0
    private lateinit var recycler: RecyclerView
    private lateinit var model: ViewModelStore

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(requireActivity())[ViewModelStore::class.java]

        recycler = view.findViewById(R.id.recycle_view)
        textView = view.findViewById(R.id.txt_total)

        arrayList = arrayListOf()
        recycler.adapter = Adapter(arrayList)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)

        model.myObject.observe (viewLifecycleOwner){

            total += it.price

            textView.text = "Total R$${total.toString().replace('.', ',')}"

            arrayList.add(it)

            recycler.adapter?.notifyDataSetChanged()
        }
    }
}