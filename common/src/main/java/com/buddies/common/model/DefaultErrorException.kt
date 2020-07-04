package com.buddies.common.model

class DefaultErrorException(
    val error: DefaultError
) : Exception()