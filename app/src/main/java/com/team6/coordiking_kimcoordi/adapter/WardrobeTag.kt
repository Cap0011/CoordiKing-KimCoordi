package com.team6.coordiking_kimcoordi.adapter

class WardrobeTag {
    var tag: Array<Boolean> = Array<Boolean>(20) { false }

    fun setIdxTrue(index: Int) {
        tag[index] = true
    }
}

