package com.buddies.common.model

enum class Animal {
    DOG, CAT, HORSE, BIRD;

    fun breeds() = Breed.values().all { it.animal == this }
}