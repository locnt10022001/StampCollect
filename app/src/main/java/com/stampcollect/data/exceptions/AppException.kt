package com.stampcollect.data.exceptions

open class AppException(
    override val message: String = "An unexpected error occurred",
    override val cause: Throwable? = null
) : Exception(message, cause)

class NoDataException(
    override val message: String = "No data found"
) : AppException(message)

class DatabaseException(
    override val message: String = "Database operation failed",
    override val cause: Throwable? = null
) : AppException(message, cause)
