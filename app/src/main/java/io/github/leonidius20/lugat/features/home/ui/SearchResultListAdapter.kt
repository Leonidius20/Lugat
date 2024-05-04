package io.github.leonidius20.lugat.features.home.ui

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.github.leonidius20.lugat.R

class SearchResultListAdapter(
    private val dataset: List<WordSearchResultUi>
): RecyclerView.Adapter<SearchResultListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // title text view, description text view

        val titleView: TextView = view.findViewById(R.id.search_result_item_title)
        val descriptionView: TextView = view.findViewById(R.id.search_result_item_description)
        val languageChip: Chip = view.findViewById(R.id.search_result_language_chip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataset[position]

        holder.titleView.text = data.title

        holder.descriptionView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data.description, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(data.description)
        }

        holder.languageChip.text = data.language
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}