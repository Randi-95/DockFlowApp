package com.example.dockflowapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dockflowapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shared = getSharedPreferences("token", MODE_PRIVATE)
        val editor = shared.edit()
        val getToken = shared.getString("token", null)

        if(getToken != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPass.text.toString()
                lifecycleScope.launch(Dispatchers.IO){
                    try{
                    val url = "${Helpers.baseUrl}Authentication/login"
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"

                        conn.setRequestProperty("Content-Type", "application/json")
                    val dataUser = JSONObject().apply {
                        put("email", email)
                        put("password", pass)
                    }

                    val os = conn.outputStream
                    os.write(dataUser.toString().toByteArray())

                    if(conn.responseCode == HttpURLConnection.HTTP_OK){
                        val input = conn.inputStream.bufferedReader().readText()

                        val response = JSONObject(input)

                        editor.putString("token", response.getString("token"))
                        editor.apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                    }else{
                        val error = conn.errorStream.bufferedReader().readText()
                        val JsonObject = JSONObject(error)
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, JsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    }
                    }catch (e : Exception){
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}