package ru.mtuci.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment() {
    private var _binding: FragmentOnboardingPageBinding? = null // Хранит ссылки на View до уничтожения интерфейса фрагмента.
    private val binding get() = requireNotNull(_binding) // Возвращает binding, проверяя, что интерфейс уже создан.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View { // Создаёт и возвращает интерфейс фрагмента по запросу Android.
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false) // Создаёт View из XML без немедленного прикрепления к container.
        return binding.root // Отдаёт Android корневую View страницы.
    }

    override fun onViewCreated(view: View, state: Bundle?) { // Заполняет элементы после создания интерфейса.
        binding.illustration.setImageResource(requireArguments().getInt(ARG_IMAGE)) // Читает ID картинки из arguments и показывает её.
        binding.title.setText(requireArguments().getInt(ARG_TITLE)) // Читает ID заголовка из arguments и устанавливает текст.
        binding.description.setText(requireArguments().getInt(ARG_DESCRIPTION)) // Читает ID описания из arguments и устанавливает текст.
    }

    override fun onDestroyView() { // Освобождает ссылки при уничтожении интерфейса фрагмента.
        _binding = null // Убирает ссылку на Binding старого интерфейса.
        super.onDestroyView() // Вызывает стандартную обработку уничтожения View.
    }

    companion object {
        private const val ARG_IMAGE = "image" // Задаёт ключ для передачи ID картинки.
        private const val ARG_TITLE = "title" // Задаёт ключ для передачи ID заголовка.
        private const val ARG_DESCRIPTION = "description" // Задаёт ключ для передачи ID описания.

        fun newInstance(page: OnboardingPage) = OnboardingPageFragment().apply { // Создаёт фрагмент и записывает в него данные страницы.
            arguments = Bundle().apply { // Создаёт контейнер аргументов и назначает его фрагменту.
                putInt(ARG_IMAGE, page.image) // Сохраняет ID картинки под ключом image.
                putInt(ARG_TITLE, page.title) // Сохраняет ID заголовка под ключом title.
                putInt(ARG_DESCRIPTION, page.description) // Сохраняет ID описания под ключом description.
            }
        }
    }
}
