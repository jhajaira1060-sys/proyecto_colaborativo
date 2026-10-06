package com.example.retocolaborativo

class UserResponse {
    data class UserResponse(
        val id: Int,
        val username: String,
        val email: String,
        val firstName: String,
        val lastName: String
    )
}