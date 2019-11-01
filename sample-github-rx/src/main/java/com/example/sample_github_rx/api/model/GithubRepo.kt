package com.example.sample_github_rx.api.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "repositories")
class GithubRepo(
        val name: String,
        @PrimaryKey @ColumnInfo(name = "full_name") @SerializedName("full_name") val fullName: String,
        @Embedded val owner: GithubOwner,
        val description: String?,
        val language: String?,
        @ColumnInfo(name = "updated_at") @SerializedName("updated_at") val updatedAt: String,
        @SerializedName("stargazers_count") val stars: Int
)
