package com.example.dockflowapp

import android.content.Context.MODE_PRIVATE
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.dockflowapp.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class profileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val shared = context?.getSharedPreferences("token", MODE_PRIVATE)
        val getToken = shared?.getString("token", "")

        lifecycleScope.launch(Dispatchers.IO){
            val url = "${Helpers.baseUrl}Me"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $getToken")

            if(conn.responseCode == HttpURLConnection.HTTP_OK){
                val input = conn.inputStream.bufferedReader().readText()
                val dataJson = JSONObject(input)

                lifecycleScope.launch(Dispatchers.Main){
                    binding.tvName.text  = dataJson.getString("name")
                    binding.tvEmail.text = dataJson.getString("email")
                    binding.tvRole.text = dataJson.getString("role")
                    binding.tvDivison.text = dataJson.getString("division")
                    binding.tvPhone.text = dataJson.getString("phoneNumber")
                    val image = dataJson.getString("photo")

                    val status = dataJson.getBoolean("isActive")
                    if(status == true){
                        binding.indicatorActive.setCardBackgroundColor(Color.GREEN)
                        binding.tvActive.setTextColor(Color.GREEN)
                    }else{
                        binding.indicatorActive.setCardBackgroundColor(Color.RED)
                        binding.tvActive.setTextColor(Color.RED)
                        binding.tvActive.text = "inactive employees"
                    }

                    lifecycleScope.launch(Dispatchers.IO){
                        val urlImg = image
                        val connImg = URL(urlImg).openStream()

                        val bitmap = BitmapFactory.decodeStream(connImg)
                        lifecycleScope.launch(Dispatchers.Main){
                            binding.ivProfile.setImageBitmap(bitmap)
                        }
                    }
                }

            }
        }

        return binding.root
    }


}