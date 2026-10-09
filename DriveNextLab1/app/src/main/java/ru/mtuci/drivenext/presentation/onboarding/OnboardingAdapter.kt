package ru.mtuci.drivenext.presentation.onboarding

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: FragmentActivity, private val pages: List<OnboardingPage>) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = pages.size // Сообщает ViewPager2 количество страниц.
    override fun createFragment(position: Int) = OnboardingPageFragment.newInstance(pages[position]) // Создаёт фрагмент с данными страницы по её индексу.
}
