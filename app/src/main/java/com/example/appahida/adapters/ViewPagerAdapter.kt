package com.example.appahida.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appahida.fragments.DayFragment
import java.util.*

class ViewPagerAdapter(fa: Fragment, private val fragments: MutableList<Calendar>) : FragmentStateAdapter(fa){
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        val fragment = DayFragment(fragments.get(position).timeInMillis)

        return fragment
    }
}