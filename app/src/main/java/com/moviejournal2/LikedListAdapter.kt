package com.moviejournal2


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class LikedListAdapter(
    private var items: List<LikedList>,
    private val onItemClick: (item:LikedList) -> Unit
):RecyclerView.Adapter<LikedListAdapter.LikedListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedListHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return LikedListHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LikedListAdapter.LikedListHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(items: List<LikedList>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class LikedListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.item_movie_poster)

        fun bind(item: LikedList) {
            Glide.with(itemView)
                .load(
                    "https://image.tmdb.org/t/p/w342${item.posterPath}"
                )
                .transform(CenterCrop())
                .into(poster)
            itemView.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }
}