package com.team6.coordiking_kimcoordi.adapter

class WardrobeTag {
    val tagList = hashMapOf("red" to 0, "orange" to 1, "yellow" to 2, "green" to 3, "blue" to 4, "navy" to 5, "purple" to 6,
            "black" to 7, "white" to 8, "grey" to 9)
    var idx: Int = 0
    var tag: Array<Boolean> = Array<Boolean>(20) { false }

    fun setIdxTrue(index: Int) {
        tag[index] = true
    }
}