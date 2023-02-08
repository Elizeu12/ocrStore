package com.belcompany.compras

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProvider(this)[ViewModelStore::class.java]

        model.myObject.observe(viewLifecycleOwner) { data ->
            total += data.price
            textView.text = "R$$total"

            arrayList = arrayListOf()

            arrayList.add(data)

            textView.text = total.toString().replace('.', ',')
            recycler.adapter?.notifyDataSetChanged()
        }

        recycler = view.findViewById(R.id.recycle_view)
        textView = view.findViewById(R.id.txt_total)

        arrayList = arrayListOf()

        recycler.adapter = Adapter(arrayList)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.myObject.removeObservers(viewLifecycleOwner)
    }

}