package com.mattmik.rapira.util

import com.mattmik.rapira.objects.shouldErrorWith
import com.mattmik.rapira.objects.shouldSucceedWith
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.IOException

class ResultTest : WordSpec({

    "zip" should {
        "return success pair" {
            val value1 = "Hello"
            val value2 = "world"
            val successResult1 = Result.Success(value1)
            val successResult2 = Result.Success(value2)

            Result.zip(successResult1, successResult2) shouldSucceedWith Pair(value1, value2)
        }

        "return error" {
            val errorMessage = "Oops."
            val successResult = Result.Success("Hello, world!")
            val errorResult = Result.Error(errorMessage)

            Result.zip(successResult, errorResult) shouldErrorWith errorMessage
            Result.zip(errorResult, successResult) shouldErrorWith errorMessage
            Result.zip(errorResult, errorResult) shouldErrorWith errorMessage
        }
    }

    "map" should {
        val transformation: (Int) -> Int = { it * 2 }

        "transform success" {
            val successResult = Result.Success(10)
            successResult.map(transformation) shouldSucceedWith 20
        }

        "not transform error" {
            val errorMessage = "Oops."
            val errorResult = Result.Error(errorMessage)
            errorResult.map(transformation) shouldErrorWith errorMessage
        }
    }

    "andThen" should {
        val transformation: (Int) -> Result<Int> = { Result.Success(it * 2) }

        "transform success" {
            val successResult = Result.Success(10)
            successResult.andThen(transformation) shouldSucceedWith 20
        }

        "not transform error" {
            val errorMessage = "Oops."
            val errorResult = Result.Error(errorMessage)
            errorResult.andThen(transformation) shouldErrorWith errorMessage
        }
    }

    "getOrThrow" should {
        val buildException: (String) -> Exception = { IOException(it) }

        "return underlying value for success" {
            val value = "Hello, world!"
            val successResult = Result.Success(value)
            successResult.getOrThrow(buildException) shouldBe value
        }

        "throw exception for errors" {
            val errorMessage = "Oops."
            val errorResult = Result.Error(errorMessage)
            val throwable = shouldThrow<IOException> {
                errorResult.getOrThrow<Unit>(buildException)
            }
            throwable.message shouldBe errorMessage
        }
    }

    "toSuccess" should {
        "convert non-result to successful result" {
            val value = "Hello, world!"
            value.toSuccess() shouldSucceedWith value
        }
    }
})
