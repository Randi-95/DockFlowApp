package com.example.dockflowapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import com.example.dockflowapp.databinding.FragmentBerandaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class berandaFragment : Fragment() {
    private lateinit var binding : FragmentBerandaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBerandaBinding.inflate(inflater, container, false)


        val shared = context?.getSharedPreferences("token", MODE_PRIVATE)
        val getToken = shared?.getString("token", "")

        lifecycleScope.launch(Dispatchers.IO){
            val url = "${Helpers.baseUrl}Attendance/greeting"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $getToken")

            val urlSummary = "${Helpers.baseUrl}Attendance/summary"
            val connSummary = URL(urlSummary).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            connSummary.setRequestProperty("Authorization", "Bearer $getToken")

            if(conn.responseCode == HttpURLConnection.HTTP_OK && conn.responseCode == HttpURLConnection.HTTP_OK){
                val input = conn.inputStream.bufferedReader().readText()
                val data = JSONObject(input)

                val inputSummary = connSummary.inputStream.bufferedReader().readText()
                val dataSummary = JSONObject(inputSummary)

                lifecycleScope.launch(Dispatchers.Main){
                    binding.tvName.text = data.getString("name")
                    binding.tvGreeting.text = data.getString("greeting")

                    binding.tvOntime.text = dataSummary.getString("ontime")
                    binding.tvLate.text = dataSummary.getString("late")
                    binding.tvAbsent.text = dataSummary.getString("absent")
                    binding.tvOvertime.text = dataSummary.getString("overtime")

                }
            }
        }

        return binding.root
    }


}