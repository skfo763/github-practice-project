package com.androidhuman.example.samplegithub.ui.search;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.androidhuman.example.samplegithub.R;
import com.androidhuman.example.samplegithub.api.model.GithubRepo;
import com.androidhuman.example.samplegithub.ui.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RepositoryHolder> {

    // RecyclerView에 띄워줄 저장소 클래스의 리스트
    private List<GithubRepo> items = new ArrayList<>();
    private ColorDrawable placeholder = new ColorDrawable(Color.GRAY);

    @Nullable
    private ItemClickListener listener;

    @NonNull
    @Override
    public RepositoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RepositoryHolder(parent);
    }

    @Override
    public void onBindViewHolder(RepositoryHolder holder, int position) {
        // 각 아이템이 viewHolder 클래스에 바인딩되었을 때 호출되는 메소드
        // 개별 아이템에 대한 ui 설정을 이 메소드에서 해줌.

        final GithubRepo repo = items.get(position);

        GlideApp.with(holder.itemView.getContext())
                .load(repo.owner.avatarUrl)
                .placeholder(placeholder)
                .into(holder.ivProfile);

        holder.tvName.setText(repo.fullName);
        holder.tvLanguage.setText(TextUtils.isEmpty(repo.language)
                // language 항목이 비어 있을 땐 R.string.no_language_specified 스트링을 참조해 띄워주고
                ? holder.itemView.getContext().getText(R.string.no_language_specified)
                // 아닐 땐 repository의 language를 띄워줌
                : repo.language);

        // 각 아이템뷰에 대한 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    // 이 클래스 안에 정의되지 않은, 다른 클래스에서 구현된 인터페이스 본체를 실행
                    listener.onItemClick(repo);
                }
            }
        });
    }

    // 아이템 개수를 반환하는 메소드
    @Override
    public int getItemCount() {
        return items.size();
    }

    // RecyclerView 에 띄워줄 데이터 리스트를 인자로 받아오는 함수 -> 아마 처음 실행될 듯.
    void setItems(@NonNull List<GithubRepo> items) {
        this.items = items;
    }

    // 타 클래스에서 상속한 listener 메소드를 클래스 내에서 사용하도록 하는 메소드.
    void setItemClickListener(@Nullable ItemClickListener listener) {
        this.listener = listener;
    }

    // 모든 아이템을 0으로 초기화 -> Adapter를 unBinding 할 때 사용
    void clearItems() {
        this.items.clear();
    }

    // 아이템뷰를 위한 component를 정의하기 위한 inner 클래스 : nested class
    // Nested class에서 static을 붙이지 않으면 상위 클래스의 메소드나 변수를 사용할 수 있게 된다.
    // 반면에 static nested class는 한 곳에서만(이 경우 itemview의 표현) 사용하는 클래스를 논리적으로 묶어서 처리하기 위해 사용한다.
    // 즉, static을 붙임으로써 상위 클래스의 메소드나 변수를 사용하지 않겠다고 명시적으로 표시하는 것이다.
    /* private 접근 제한자를 사용하지 않는 이유는, 이 클래스의 상위클래스인 SearchAdapter가 RecyclerView.Aapter를 상속받고
       ViewHolder 클래스를 제네릭을 받는 부분(클래스 선언부: 클래스 외부)에서 이 클래스에 접근하고 있기 때문 */
    static class RepositoryHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName;
        TextView tvLanguage;

        RepositoryHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_repository, parent, false));

            ivProfile = itemView.findViewById(R.id.ivItemRepositoryProfile);
            tvName = itemView.findViewById(R.id.tvItemRepositoryName);
            tvLanguage = itemView.findViewById(R.id.tvItemRepositoryLanguage);
        }
    }

    // onclick 시 동작을 위한 메소드 오버라이딩 강제를 요청하는 인터페이스.
    public interface ItemClickListener {
        void onItemClick(GithubRepo repository);
    }
}