package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.errors.RapiraIncorrectArgumentCountError
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import kotlin.math.absoluteValue
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class NativeFunctionsTest : WordSpec() {
    init {
        lateinit var environment: Environment
        val mockArgument = mockk<Argument>()

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

        "sign" should {
            val function = nativeFunctions["sign"] as RapiraCallable

            "return sign for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe RInteger(num.sign)
                }
            }

            "return sign for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe RInteger(num.sign.toInt())
                }
            }
        }

        "sqrt" should {
            val function = nativeFunctions["sqrt"] as RapiraCallable

            "return square root for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe sqrt(num.toDouble()).toReal()
                }
            }

            "return square root for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe sqrt(num).toReal()
                }
            }
        }

        "index" When {
            val function = nativeFunctions["index"] as RapiraCallable
            val mockArgument2 = mockk<Argument>()

            "given a sequence" should {
                val sequence = listOf(
                    Text("Hello, world!"),
                    RInteger(123),
                    Real(1.23)
                ).toSequence()

                "return index if element is present" {
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(123))
                    every { mockArgument2.evaluate(any()) } returns SimpleVariable(sequence)

                    val result = function.call(environment, listOf(mockArgument, mockArgument2))

                    result shouldBe RInteger(2)
                }

                "return 0 if element is not present" {
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(LogicalYes)
                    every { mockArgument2.evaluate(any()) } returns SimpleVariable(sequence)

                    val result = function.call(environment, listOf(mockArgument, mockArgument2))

                    result shouldBe RInteger(0)
                }
            }

            "given text" should {
                val text = "Hello, world!".toText()

                "return index if substring is present" {
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Text("lo, wo"))
                    every { mockArgument2.evaluate(any()) } returns SimpleVariable(text)

                    val result = function.call(environment, listOf(mockArgument, mockArgument2))

                    result shouldBe RInteger(4)
                }

                "return 0 if substring is not present" {
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Text("okay"))
                    every { mockArgument2.evaluate(any()) } returns SimpleVariable(text)

                    val result = function.call(environment, listOf(mockArgument, mockArgument2))

                    result shouldBe RInteger(0)
                }
            }
        }

        "sin" should {
            val function = nativeFunctions["sin"] as RapiraCallable

            "return sin for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe sin(num.toDouble()).toReal()
                }
            }

            "return sin for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe sin(num).toReal()
                }
            }
        }

        "cos" should {
            val function = nativeFunctions["cos"] as RapiraCallable

            "return cos for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe cos(num.toDouble()).toReal()
                }
            }

            "return cos for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe cos(num).toReal()
                }
            }
        }

        "tg" should {
            val function = nativeFunctions["tg"] as RapiraCallable

            "return tan for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe tan(num.toDouble()).toReal()
                }
            }

            "return tan for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe tan(num).toReal()
                }
            }
        }

        "arcsin" should {
            val function = nativeFunctions["arcsin"] as RapiraCallable

            "return arcsin for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe asin(num.toDouble()).toReal()
                }
            }

            "return arcsin for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe asin(num).toReal()
                }
            }
        }

        "arctg" should {
            val function = nativeFunctions["arctg"] as RapiraCallable

            "return arctan for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe atan(num.toDouble()).toReal()
                }
            }

            "return arctan for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe atan(num).toReal()
                }
            }
        }

        "exp" should {
            val function = nativeFunctions["exp"] as RapiraCallable

            "return exp for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe exp(num.toDouble()).toReal()
                }
            }

            "return exp for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe exp(num).toReal()
                }
            }
        }

        "ln" should {
            val function = nativeFunctions["ln"] as RapiraCallable

            "return ln for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe ln(num.toDouble()).toReal()
                }
            }

            "return ln for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe ln(num).toReal()
                }
            }
        }

        "lg" should {
            val function = nativeFunctions["lg"] as RapiraCallable

            "return log for integers" {
                checkAll<Int> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(RInteger(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe log10(num.toDouble()).toReal()
                }
            }

            "return log for real numbers" {
                checkAll<Double> { num ->
                    every { mockArgument.evaluate(any()) } returns SimpleVariable(Real(num))

                    val result = function.call(environment, listOf(mockArgument))

                    result shouldBe log10(num).toReal()
                }
            }
        }
    }
}
