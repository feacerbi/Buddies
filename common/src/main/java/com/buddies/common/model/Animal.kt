package com.buddies.common.model

enum class Animal {
    DOG, CAT;

    fun breeds() = Breed.values().all { it.animal == this }
}