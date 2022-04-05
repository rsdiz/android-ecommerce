package id.rsdiz.rdshop.seller.ui.home.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.seller.databinding.FragmentTrackingBinding

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding as FragmentTrackingBinding

    private val dataArgs get() = TrackingFragmentArgs.fromBundle(arguments as Bundle)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url =
            StringBuilder("https://cekresi.com/?v=wi1&noresi=+")
                .append(dataArgs.trackingNumber)
                .toString()

        binding.apply {
            toolbar.title =
                if (dataArgs.status.toShort() == Consts.STATUS_DISPATCH) "Lacak Pengiriman"
                else "Detail Pengiriman"

            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }

            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
