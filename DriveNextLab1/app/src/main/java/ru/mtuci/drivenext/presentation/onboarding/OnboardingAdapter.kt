package ru.mtuci.drivenext.presentation.onboarding

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// Связывает данные страниц с фрагментами ViewPager2.
class OnboardingAdapter(activity: FragmentActivity, private val pages: List<OnboardingPage>) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = pages.size
    override fun createFragment(position: Int) = OnboardingPageFragment.newInstance(pages[position])
}
