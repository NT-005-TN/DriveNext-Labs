package ru.mtuci.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {
    // Binding существует только между onCreateView и onDestroyView.
    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        // false означает, что FragmentManager сам прикрепит View к контейнеру.
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        // Читаем ID ресурсов из arguments и заполняем элементы страницы.
        binding.illustration.setImageResource(requireArguments().getInt(ARG_IMAGE))
        binding.title.setText(requireArguments().getInt(ARG_TITLE))
        binding.description.setText(requireArguments().getInt(ARG_DESCRIPTION))
    }

    override fun onDestroyView() {
        // Убираем ссылку, чтобы не удерживать уничтоженную View.
        _binding = null
        super.onDestroyView()
    }

    companion object {
        // Константы исключают расхождение ключей при записи и чтении Bundle.
        private const val ARG_IMAGE = "image"
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(page: OnboardingPage) = OnboardingPageFragment().apply {
            // Фабрика создаёт Fragment вместе с полным набором arguments.
            arguments = Bundle().apply {
                putInt(ARG_IMAGE, page.image)
                putInt(ARG_TITLE, page.title)
                putInt(ARG_DESCRIPTION, page.description)
            }
        }
    }
}
