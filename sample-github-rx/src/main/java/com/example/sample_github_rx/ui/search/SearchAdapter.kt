package com.example.sample_github_rx.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_github_rx.R
import com.example.sample_github_rx.api.model.GithubRepo
import com.example.sample_github_rx.ui.GlideApp
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {
    var items: MutableList<GithubRepo> = ArrayList()
    var listener: ItemClickListener? = null
    private val placeholder = ColorDrawable(Color.GRAY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
        return RepositoryHolder(parent)
    }

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        val repo = items[position]
        setItemViewUi(repo, holder)
    }

    private fun setItemViewUi(repo: GithubRepo, holder: RepositoryHolder) {
        GlideApp.with(holder.itemView.context)
                .load(repo.owner.avatarUrl)
                .placeholder(placeholder)
                .into(holder.ivProfile)

        holder.apply {
            tvName.text = repo.fullName
            tvLanguage.text =
                    if(TextUtils.isEmpty(repo.language)) holder.itemView.context.getText(R.string.no_language_specified)
                    else repo.language

            itemView.setOnClickListener {
                listener?.onItemClick(repo)
            }
        }
    }

    fun clearItems() {
        items.clear()
    }

    // 아이템 개수를 반환하는 메소드
    override fun getItemCount(): Int {
        return items.size
    }

    class RepositoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repository, parent, false)) {
        var ivProfile: ImageView = itemView.findViewById(R.id.ivItemRepositoryProfile)
        var tvName: TextView = itemView.findViewById(R.id.tvItemRepositoryName)
        var tvLanguage: TextView = itemView.findViewById(R.id.tvItemRepositoryLanguage)
    }

    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}