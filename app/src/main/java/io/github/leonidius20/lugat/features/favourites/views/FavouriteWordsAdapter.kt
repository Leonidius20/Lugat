package io.github.leonidius20.lugat.features.favourites.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.leonidius20.lugat.databinding.FavouriteWordListItemBinding
import io.github.leonidius20.lugat.features.common.ui.htmlSpannable
import io.github.leonidius20.lugat.features.favourites.models.FavouriteWordsUiState

class FavouriteWordsAdapter(
    private val dataset: ArrayList<FavouriteWordsUiState.Loaded.WordBeingLearnedUi>,
    private val onItemClick: (FavouriteWordsUiState.Loaded.WordBeingLearnedUi) -> Unit,
) : RecyclerView.Adapter<FavouriteWordsAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: FavouriteWordListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: FavouriteWordsUiState.Loaded.WordBeingLearnedUi) {
            binding.searchResultItemTitle.text = "${word.latin} (${word.cyrillic})"
            binding.searchResultItemDescription.text = htmlSpannable(word.description)
            binding.root.setOnClickListener {
                onItemClick(word)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavouriteWordListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = dataset[position]
        holder.bind(word)
    }

    override fun getItemCount() = dataset.size

}