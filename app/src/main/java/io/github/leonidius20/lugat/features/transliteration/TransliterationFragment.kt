package io.github.leonidius20.lugat.features.transliteration

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.databinding.FragmentTransliterationBinding
import io.github.leonidius20.lugat.domain.interactors.transliterate.TransliterationInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class TransliterationFragment : Fragment() {

    private var _binding: FragmentTransliterationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransliterationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel by viewModels<TransliterationViewModel>()

        binding.transliterationSourceText.addTextChangedListener {
            viewModel.transliterate(it.toString())
        }

        binding.transliterationChangeDirectionButton.setOnClickListener {
            viewModel.toggleDirection()
        }

        binding.transliterationScreenClearButton.setOnClickListener {
            binding.transliterationSourceText.setText("")
        }

        val clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        binding.transliterationScreenCopyButton.setOnClickListener {
            val clip = ClipData.newPlainText("Transliterated", viewModel.uiState.value.targetText)
            clipboard.setPrimaryClip(clip)
        }

        binding.transliterationScreenPasteButton.setOnClickListener {
            if (!clipboard.hasPrimaryClip()) return@setOnClickListener

            if (clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true) {
                val text = clipboard.primaryClip?.getItemAt(0)?.text
                binding.transliterationSourceText.setText(text)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.onEach {
                    with(binding) {
                        transliterationTargetText.text = it.targetText
                        transliterationScreenClearButton.isVisible = it.isClearButtonVisible
                        transliterationScreenCopyButton.isVisible = it.isCopyButtonVisible
                        transliterationScreenPasteButton.isVisible = it.isPasteButtonVisible
                    }

                }.launchIn(this)

                viewModel.direction.onEach {
                    when(it) {
                        TransliterationInteractor.Direction.CYRILLIC_TO_LATIN -> {
                            binding.sourceAlphabetText.setText(R.string.transliteration_screen_cyrillic)
                            binding.targetAlphabetText.setText(R.string.transliteration_screen_latin)
                        }
                        TransliterationInteractor.Direction.LATIN_TO_CYRILLIC -> {
                            binding.sourceAlphabetText.setText(R.string.transliteration_screen_latin)
                            binding.targetAlphabetText.setText(R.string.transliteration_screen_cyrillic)
                        }
                    }
                }.launchIn(this)
            }
        }


        binding.transliterationScreenTopAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}