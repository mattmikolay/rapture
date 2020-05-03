package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import kotlin.math.absoluteValue

class NativeFunctionsTest : WordSpec() {
    init {
        lateinit var environment: Environment

        beforeTest {
            environment = Environment()
        }

        "all native functions" should {
            "throw exception when invoked with invalid number of arguments" {
                forAll(
                    row("abs", 1),
                    row("sign", 1),
                    row("sqrt", 1),
                    row("entier", 1),
                    row("round", 1),
                    row("rand", 1),
                    row("int_rand", 1),
                    row("index", 2),
                    row("is_empty", 1),
                    row("is_log", 1),
                    row("is_int", 1),
                    row("is_real", 1),
                    row("is_text", 1),
                    row("is_seq", 1),
                    row("is_proc", 1),
                    row("is_fun", 1),
                    row("sin", 1),
                    row("cos", 1),
                    row("tg", 1),
                    row("arcsin", 1),
                    row("arctg", 1),
                    row("exp", 1),
                    row("ln", 1),
                    row("lg", 1)
                ) { functionName, expectedArgumentCount ->
                    shouldThrow<RapiraIncorrectArgumentCountError> {
                        (nativeFunctions[functionName] as RapiraCallable).call(environment, emptyList())
                    }
                }
            }
        }

        "abs" should {
            val function = nativeFunctions["abs"] as RapiraCallable
            val mockArgument = mockk<Argument>()

            "return absolute value for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe RInteger(num.absoluteValue)
                }
            }

            "return absolute value for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe Real(num.absoluteValue)
                }
            }
        }
    }
}
