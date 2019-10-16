package com.example.sample_github_mvp.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sample_github_mvp.R
import com.example.sample_github_mvp.view.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // java와 달리, 코틀린 안드로이드 익스텐션으로 findViewById 사용하지 않고 바로 맵핑 가능
        btnActivityMainSearch.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
    }
}
