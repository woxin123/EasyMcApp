package top.mcwebsite.novel.ui.read

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.mcwebsite.novel.R
import top.mcwebsite.novel.ui.read.page.PageMode

class PageModeAdapter(
    private val pageModes: List<PageMode>,
    private var currentSelected: Int = 0,
    private val onItemClickListener: (PageMode) -> Unit
) : RecyclerView.Adapter<PageModeAdapter.PageModeViewHolder>() {

    class PageModeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.page_mode)
        val selectedView = itemView.findViewById<View>(R.id.selected_view)
    }

    init {
        pageModes.forEachIndexed { index, item ->
            if (item.ordinal == currentSelected) {
                currentSelected = index
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageModeViewHolder {
        return PageModeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_page_mode, parent, false))
    }

    override fun onBindViewHolder(holder: PageModeViewHolder, position: Int) {
        holder.textView.text = pageModes[position].pageModeName
        holder.textView.setOnClickListener {
            val previewChoice = currentSelected
            currentSelected = position
            notifyItemChanged(previewChoice)
            notifyItemChanged(currentSelected)
            onItemClickListener.invoke(pageModes[position])
        }
        holder.selectedView.visibility = if (currentSelected == position) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return pageModes.size
    }
}
