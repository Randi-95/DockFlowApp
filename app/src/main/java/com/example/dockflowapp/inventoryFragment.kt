package com.example.dockflowapp

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dockflowapp.databinding.CardItemBinding
import com.example.dockflowapp.databinding.FragmentInventoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.http.params.HttpParams
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class inventoryFragment : Fragment() {
    private lateinit var binding : FragmentInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)

        val shared = context?.getSharedPreferences("token", MODE_PRIVATE)
        val getToken = shared?.getString("token", "")

        binding.btnCart.setOnClickListener { 
            startActivity(Intent(context, CartActivity::class.java))
        }
        
        var filter = ""

        loadData(getToken, inflater, filter)

        binding.tvSearch.doAfterTextChanged { text -> loadData(getToken, inflater, text.toString(), filter) }
        binding.cFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            if(checkedIds.isNotEmpty()){
                when(checkedIds[0]){
                    binding.cAll.id -> {
                        filter = ""
                        loadData(getToken, inflater, binding.tvSearch.text.toString(), filter)
                    }
                    binding.cTools.id -> {
                        filter = "Tools"
                        loadData(getToken, inflater, binding.tvSearch.text.toString(), filter)
                    }
                    binding.cSafety.id -> {
                        filter = "Safety"
                        loadData(getToken, inflater, binding.tvSearch.text.toString(), filter)
                    }
                    binding.cSparepart.id -> {
                        filter = "Sparepart"
                        loadData(getToken, inflater, binding.tvSearch.text.toString(), filter)
                    }
                    binding.cConsumable.id -> {
                        filter = "Consumable"
                        loadData(getToken, inflater, binding.tvSearch.text.toString(), filter)
                    }
                }
            }
        }

        return binding.root
    }

    private fun loadData(getToken: String?, inflater: LayoutInflater, search: String = "", filter: String = "") {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = "${Helpers.baseUrl}Items?search=${search}&filter=$filter"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $getToken")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val input = conn.inputStream.bufferedReader().readText()
                val dataArray = JSONArray(input)

                lifecycleScope.launch(Dispatchers.Main) {
                    binding.rvItem.adapter = object : RecyclerView.Adapter<vhItem>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vhItem {
                            return vhItem(CardItemBinding.inflate(inflater, parent, false))
                        }

                        override fun onBindViewHolder(holder: vhItem, position: Int) {
                            dataArray.getJSONObject(position).let { x ->
                                holder.bind.tvName.text = x.getString("name")
                                holder.bind.tvSku.text = "SKU: ${x.getString("sku")}"
                                holder.bind.tvLocation.text = x.getString("rackLocation")
                                holder.bind.tvStatus.text = x.getString("status")
                            }
                        }

                        override fun getItemCount(): Int {
                            return dataArray.length()
                        }
                    }

                    binding.rvItem.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }


    class vhItem(val bind : CardItemBinding) : RecyclerView.ViewHolder(bind.root)
}