package com.android.flags.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.android.flags.presentation.QuizAdapter
import com.android.flags.presentation.QuizFragment
import javax.inject.Inject

class FragmentFactory @Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            QuizFragment::class.java.name -> QuizFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}