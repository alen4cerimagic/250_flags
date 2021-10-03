package com.android.flags.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.flags.adapters.CountryAdapter
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class FragmentFactory @Inject constructor(
    private val glide: RequestManager,
    private val countryAdapter: CountryAdapter
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            MainFragment::class.java.name -> MainFragment(null, glide, countryAdapter)
            else -> super.instantiate(classLoader, className)
        }
    }
}