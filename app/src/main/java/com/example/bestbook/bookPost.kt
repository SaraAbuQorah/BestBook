package com.example.bestbook

data class bookPost(val bookName: String="",
                    val authorName: String="",
                    val pageNum: String="",
                    val originalCopy: Boolean=false,
                    val language: String="",
                    val price: String="",
                    val exchange:Boolean=false,
                    val category: String="",
                    val description: String="",
                    val bookerEmail:String="",
                    val bookerPhone:String="",
                    var imageUri:String="",
                    var favImg:String="")
