package com.example.practicekotlin14.home

data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    // firebase의 realTimeDatabase를 그대로 모델 클래스로 사용하기 위해서는 빈 생성자 생성이 필수
    constructor(): this("", "", 0, "", "") // 빈 생성자 생성후 기본 값으로 초기화
}