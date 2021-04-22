package com.hardrelice.pixivzh.widget.addons

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hardrelice.pixivzh.widget.ImageDialog

class ImageDialogWebViewClient(val webView: WebView):WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webView.visibility = View.VISIBLE
    }
}