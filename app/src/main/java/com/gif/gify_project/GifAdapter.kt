package com.gif.gify_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GifAdapter(
    private val gifs:MutableList<Gif>
    ) : RecyclerView.Adapter<GifAdapter.GifViewHolder>(){

    class GifViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        return GifViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.one_gif,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val curGif = gifs[position]

        holder.itemView.apply {
            val singleGif: ImageView = findViewById(R.id.ibSingleGif)
            Glide.with(this)
                .load(curGif.images.original.url)
                .placeholder(R.drawable.ic_loading)//hourglass loading image
                .into(singleGif)
        }
    }

    override fun getItemCount(): Int {
        return gifs.size
    }
}