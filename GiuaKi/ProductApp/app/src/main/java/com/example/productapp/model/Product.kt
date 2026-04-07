package com.example.productapp.model

data class Product(
    var id: String = "",
    val name: String = "",
    val type: String = "",
    val price: Long = 0,
    val image: String = "" // Lưu chuỗi Base64 của ảnh
) {
    constructor() : this("", "", "", 0, "")
}
