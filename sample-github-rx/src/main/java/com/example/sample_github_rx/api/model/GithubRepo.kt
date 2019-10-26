package com.example.sample_github_rx.api.model

import com.google.gson.annotations.SerializedName

class GithubRepo(
        val name: String, @field:SerializedName("full_name")
        val fullName: String, val owner: GithubOwner, val description: String, val language: String,
        @field:SerializedName("updated_at")
        val updatedAt: String, @field:SerializedName("stargazers_count")
        val stars: Int
)
