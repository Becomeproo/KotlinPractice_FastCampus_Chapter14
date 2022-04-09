package com.example.practicekotlin14.chatdetail

data class ChatItem(
    val sendId: String,
    val message: String
) {
    constructor(): this("", "")
}