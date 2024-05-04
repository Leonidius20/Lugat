package io.github.leonidius20.lugat.features.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.features.home.HomeFragment
import net.nicbell.materiallists.ListItem

class MenuAdapter(
    private val items: List<HomeFragment.MenuItem>
): Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(val view: ListItem) : RecyclerView.ViewHolder(view) {

        val iconView: AppCompatImageView = view.findViewById(R.id.menu_item_leading_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)

        return ViewHolder(view as ListItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.view.setOnClickListener {
            item.action()
        }

        holder.view.headline.text = item.title

        holder.iconView.setImageResource(item.icon)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}