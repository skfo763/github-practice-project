package com.example.sample_github_kotlin.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample_github_kotlin.R
import com.example.sample_github_kotlin.api.model.GithubRepo
import com.example.sample_github_kotlin.ui.GlideApp
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.RepositoryHolder>() {

    // RecyclerView 에 띄워줄 저장소 클래스의 리스트
    var items: MutableList<GithubRepo> = ArrayList()
    var listener: ItemClickListener? = null
    private val placeholder = ColorDrawable(Color.GRAY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
        return RepositoryHolder(parent)
    }

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        // 각 아이템이 viewHolder 클래스에 바인딩되었을 때 호출되는 메소드
        // 개별 아이템에 대한 ui 설정을 이 메소드에서 해줌.
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

            // 각 아이템뷰에 대한 클릭 이벤트
            itemView.setOnClickListener {
                // 이 클래스 안에 정의되지 않은, 다른 클래스에서 구현된 인터페이스 본체를 실행
                listener?.onItemClick(repo)
            }
        }
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

    // onclick 시 동작을 위한 메소드 오버라이딩 강제를 요청하는 인터페이스.
    interface ItemClickListener {
        fun onItemClick(repository: GithubRepo)
    }
}