package io.github.leonidius20.lugat.features.home.ui

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.leonidius20.lugat.databinding.SearchResultItemBinding

class SearchResultListAdapter(
    private val dataset: List<WordSearchResultUi>,
    private val onItemClick: (WordSearchResultUi) -> Unit,
): RecyclerView.Adapter<SearchResultListAdapter.ViewHolder>() {

    class ViewHolder(val binding: SearchResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // title text view, description text view

        val titleView = binding.searchResultItemTitle
        val descriptionView = binding.searchResultItemDescription
        val languageChip = binding.searchResultLanguageChip
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
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

        holder.binding.root.setOnClickListener {
            onItemClick(data)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

}