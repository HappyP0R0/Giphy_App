package com.gif.gify_project

data class GifHolder(
    val data:List<Gif>,
    val pagination:PaginationData,
    val meta:MetaDataClass
)
