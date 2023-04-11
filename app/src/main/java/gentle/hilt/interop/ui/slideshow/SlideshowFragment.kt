package gentle.hilt.interop.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.FragmentSlideshowBinding

@AndroidEntryPoint
class SlideshowFragment : Fragment() {
    private val viewModel: SlideshowViewModel by viewModels()
    lateinit var binding: FragmentSlideshowBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlideshowBinding.inflate(layoutInflater)
        return binding.root
    }
}
