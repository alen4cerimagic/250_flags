package com.android.flags.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.flags.R
import com.android.flags.util.FragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}