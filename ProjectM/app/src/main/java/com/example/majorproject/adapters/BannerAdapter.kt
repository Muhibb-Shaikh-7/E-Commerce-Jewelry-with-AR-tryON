package com.example.majorproject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class BannerAdapter(private val bannerImages: List<String>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        // Load the image using Glide
        Glide.with(holder.bannerImage.context)
            .load(bannerImages[position])
            .centerCrop()
            .error(R.drawable.model_img_1)
            .into(holder.bannerImage)
    }

    override fun getItemCount() = bannerImages.size

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerImage: ImageView = itemView.findViewById(R.id.bannerImage)
    }
}
