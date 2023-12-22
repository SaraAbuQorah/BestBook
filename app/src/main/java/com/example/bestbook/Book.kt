package com.example.bestbook

import android.os.Parcel
import android.os.Parcelable

data class Book(val bookId : String?="",
                val bookName: String?="",
                val authorName: String?="",
                val pageNum: String?="",
                val originalCopy: Boolean?=false,
                val language: String?="",
                val price: String?="",
                val exchange:Boolean?=false,
                val category: String?="",
                val description: String?="",
                val bookerEmail:String?="",
                val bookerPhone:String?="",
                var imageUri:String?=""
                 ) :  Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookId)
        parcel.writeString(bookName)
        parcel.writeString(authorName)
        parcel.writeString(pageNum)
        parcel.writeByte(if (originalCopy == true) 1 else 0)
        parcel.writeString(language)
        parcel.writeString(price)
        parcel.writeByte(if (exchange == true) 1 else 0)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(bookerEmail)
        parcel.writeString(bookerPhone)
        parcel.writeString(imageUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }

}


