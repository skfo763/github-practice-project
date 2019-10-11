package com.example.sample_github_kotlin.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.ui.search.SearchActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    internal lateinit var btnSearch: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch = findViewById(R.id.btnActivityMainSearch)

        // 버튼 setOnClickListener 등록
        btnSearch.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
    }
}
