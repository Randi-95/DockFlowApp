package com.example.dockflowapp

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dockflowapp.databinding.CardHistoryBinding
import com.example.dockflowapp.databinding.FragmentRiwayatBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class riwayatFragment : Fragment() {
   private lateinit var binding : FragmentRiwayatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRiwayatBinding.inflate(inflater, container, false)

        val shared = context?.getSharedPreferences("token", MODE_PRIVATE)
        val getToken = shared?.getString("token", "")


        loadData(getToken, inflater, container)

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId){
                    R.id.btnAll -> {
                        loadData(getToken, inflater, container)
                    }
                    R.id.btnAccepted -> {
                        loadData(getToken, inflater, container, "Approved")
                    }
                }
            }
        }

        return binding.root
    }

    private fun loadData(
        getToken: String?,
        inflater: LayoutInflater,
        container: ViewGroup?,
        filter: String = ""
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = "${Helpers.baseUrl}Bookings?filter=$filter"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $getToken")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val input = conn.inputStream.bufferedReader().readText()
                val dataArray = JSONArray(input)

                lifecycleScope.launch(Dispatchers.Main) {
                    binding.rvHistory.adapter = object : RecyclerView.Adapter<historyVH>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): historyVH {
                            return historyVH(CardHistoryBinding.inflate(inflater, container, false))
                        }

                        override fun onBindViewHolder(holder: historyVH, position: Int) {
                            dataArray.getJSONObject(position).let { x ->
                                holder.bind.codeBooking.text = x.getString("bookingCode")
                                holder.bind.tvName.text = x.getString("nameItems")
                                holder.bind.tvRequestDate.text = x.getString("requestDate")
                                holder.bind.tvStatus.text = x.getString("descriptionStatus")
                                val status = x.getString("status")

                                if (status == "Rejected") {
                                    holder.bind.strokeStatus.setCardBackgroundColor(
                                        Color.parseColor(
                                            "#FF5C5C"
                                        )
                                    )
                                    holder.bind.tvStatus.setTextColor(Color.parseColor("#FF5C5C"))
                                    holder.bind.bgStatus.setCardBackgroundColor(Color.parseColor("#FFEDEd"))
                                    holder.bind.cvReason.visibility = View.VISIBLE
                                    holder.bind.tvRason.text =
                                        "Reason: ${x.getString("rejectReason")}"
                                    holder.bind.btnShowQr.visibility = View.GONE
                                } else if (status == "Pending") {
                                    holder.bind.strokeStatus.setCardBackgroundColor(
                                        Color.parseColor(
                                            "#FFB300"
                                        )
                                    )
                                    holder.bind.tvStatus.setTextColor(Color.parseColor("#FFB300"))
                                    holder.bind.bgStatus.setCardBackgroundColor(Color.parseColor("#FFF8E1"))
                                    holder.bind.approver.visibility = View.VISIBLE
                                    holder.bind.btnShowQr.visibility = View.GONE
                                } else {
                                    holder.bind.strokeStatus.setCardBackgroundColor(
                                        Color.parseColor(
                                            "#00C896"
                                        )
                                    )
                                    holder.bind.tvStatus.setTextColor(Color.parseColor("#00C896"))
                                    holder.bind.bgStatus.setCardBackgroundColor(Color.parseColor("#E0F2F1"))
                                }
                            }
                        }

                        override fun getItemCount(): Int {
                            return dataArray.length()
                        }
                    }

                    binding.rvHistory.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    class historyVH(val bind: CardHistoryBinding) : RecyclerView.ViewHolder(bind.root)
}