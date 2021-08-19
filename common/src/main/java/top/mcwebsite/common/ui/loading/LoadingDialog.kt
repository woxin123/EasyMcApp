package top.mcwebsite.common.ui.loading

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import top.mcwebsite.common.R

class LoadingDialog constructor(
    context: Context,
) : Dialog(context) {

    private val progressBar by lazy {  findViewById<ProgressBar>(R.id.progress_bar) }

    private val title by lazy { findViewById<TextView>(R.id.title) }

    private val close by lazy { findViewById<ImageView>(R.id.close) }

    private var cancel: () -> Unit = {}


    init {
        setContentView(R.layout.layout_loading_view)
        setCanceledOnTouchOutside(false)

        close.setOnClickListener {
            dismiss()
            cancel.invoke()
        }
    }

    fun showLoading(title: String, delayShowClose: Long = 0, cancel: (() -> Unit)? = null) {
        close.postDelayed({
            close.visibility = View.VISIBLE
        }, delayShowClose)
        this.title.text = title
        if (cancel != null) {
            this.cancel = cancel
        }
        show()
    }

    fun setCancel(cancel: () -> Unit) {
        this.cancel = cancel
    }

}