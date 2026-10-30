package ru.mtuci.drivenext.presentation.onboarding

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: FragmentActivity, private val pages: List<OnboardingPage>) :
    FragmentStateAdapter(activity) {
    // ViewPager2 запрашивает общее количество страниц.
    override fun getItemCount(): Int = pages.size
    // Для позиции создаём Fragment с соответствующими ресурсами.
    override fun createFragment(position: Int) = OnboardingPageFragment.newInstance(pages[position])
}
