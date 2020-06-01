package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Logical
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RObject
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toReal
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ExpressionVisitorTest : WordSpec({
    lateinit var environment: Environment

    fun evaluateExpression(input: String): RObject? {
        val lexer = RapiraLexer(CharStreams.fromString(input))
        val parser = RapiraParser(CommonTokenStream(lexer))
        val tree = parser.expression()
        return ExpressionVisitor(environment).visit(tree)
    }

    beforeTest {
        environment = Environment()
        environment["alpha"] = SimpleVariable("Ready!".toText())
        environment["Bravo"] = SimpleVariable("Set!".toText())
    }

    "visit" should {
        "evaluate variables" {
            val resultObject1 = evaluateExpression("alpha")
            val resultObject2 = evaluateExpression("Bravo")
            resultObject1 shouldBe "Ready!".toText()
            resultObject2 shouldBe "Set!".toText()
        }

        "evaluate text" {
            val resultObject = evaluateExpression("\"Hello, world!\"")
            resultObject shouldBe "Hello, world!".toText()
        }

        "evaluate unsigned integer numbers" {
            val resultObject = evaluateExpression("12345")
            resultObject shouldBe 12345.toRInteger()
        }

        "evaluate unsigned real numbers" {
            val resultObject = evaluateExpression("12.345")
            resultObject shouldBe (12.345).toReal()
        }

        "evaluate procedure definitions" {
            val resultObject = evaluateExpression("""
                proc (param1, =>param2, <=param3)
                    output: "Hello, world!"
                    test := 3 + 4
                end
            """.trimIndent())
            resultObject should beOfType<Procedure>()
        }

        "evaluate function definitions" {
            val resultObject = evaluateExpression("""
                fun (param1, =>param2)
                    output: "Hello, world!"
                    test := 3 + 4
                end
            """.trimIndent())
            resultObject should beOfType<Function>()
        }

        "evaluate sequences" {
            val resultObject = evaluateExpression(
                "<* 1, 2, 3, \"a\", \"b\", \"c\" *>"
            )
            resultObject shouldBe listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger(),
                "a".toText(),
                "b".toText(),
                "c".toText()
            ).toSequence()
        }

        "evaluate length" {
            val resultObject = evaluateExpression("#\"Hello\"")
            resultObject shouldBe "Hello".length.toRInteger()
        }

        "evaluate index expressions with commas" {
            val resultObject1 = evaluateExpression("<* 100, 200, 300 *>[2]")
            resultObject1 shouldBe 200.toRInteger()
            val resultObject2 = evaluateExpression("<* 100, <* 2000, 3000, 4000, 5000 *>, 300 *>[2][4]")
            resultObject2 shouldBe 5000.toRInteger()
        }

        "evaluate index expressions with colons" {
            val resultObject1 = evaluateExpression("<* 100, 200, 300 *>[:]")
            resultObject1 shouldBe listOf(
                100.toRInteger(),
                200.toRInteger(),
                300.toRInteger()
            ).toSequence()
            val resultObject2 = evaluateExpression("<* 100, 200, 300 *>[2:3]")
            resultObject2 shouldBe listOf(
                200.toRInteger(),
                300.toRInteger()
            ).toSequence()
            val resultObject3 = evaluateExpression("<* 100, 200, 300 *>[2:]")
            resultObject3 shouldBe listOf(
                200.toRInteger(),
                300.toRInteger()
            ).toSequence()
            val resultObject4 = evaluateExpression("<* 100, 200, 300 *>[:2]")
            resultObject4 shouldBe listOf(
                100.toRInteger(),
                200.toRInteger()
            ).toSequence()
        }

        "evaluate function calls" {
            val resultObject1 = evaluateExpression("is_text(\"Hello\")")
            resultObject1 shouldBe Logical(true)
            val resultObject2 = evaluateExpression("abs(=>-123)")
            resultObject2 shouldBe 123.toRInteger()
        }

        "evaluate unary positive" {
            val resultObject = evaluateExpression("+12345")
            resultObject shouldBe 12345.toRInteger()
        }

        "evaluate unary negative" {
            val resultObject = evaluateExpression("-12345")
            resultObject shouldBe (-12345).toRInteger()
        }

        "evaluate addition" {
            val resultObject = evaluateExpression("2 + 4")
            resultObject shouldBe 6.toRInteger()
        }

        "evaluate subtraction" {
            val resultObject = evaluateExpression("50 - 25")
            resultObject shouldBe 25.toRInteger()
        }

        "evaluate multiplication" {
            val resultObject = evaluateExpression("2 * 12")
            resultObject shouldBe 24.toRInteger()
        }

        "evaluate division" {
            val resultObject = evaluateExpression("100 / 25")
            resultObject shouldBe 4.toRInteger()
        }

        "evaluate integer division" {
            val resultObject = evaluateExpression("7 // 3")
            resultObject shouldBe 2.toRInteger()
        }

        "evaluate modulo" {
            val resultObject = evaluateExpression("7 /% 3")
            resultObject shouldBe 1.toRInteger()
        }

        "evaluate exponentiation" {
            val resultObject = evaluateExpression("3 ** 4")
            resultObject shouldBe 81.toRInteger()
        }

        "evaluate equality" {
            val resultObject = evaluateExpression("1 = 1")
            resultObject shouldBe Logical(true)
        }

        "evaluate inequality" {
            val resultObject = evaluateExpression("1 /= 1")
            resultObject shouldBe Logical(false)
        }

        "evaluate < relation" {
            val resultObject = evaluateExpression("4 < 5")
            resultObject shouldBe Logical(true)
        }

        "evaluate > relation" {
            val resultObject = evaluateExpression("4 > 5")
            resultObject shouldBe Logical(false)
        }

        "evaluate <= relation" {
            val resultObject = evaluateExpression("4 <= 4")
            resultObject shouldBe Logical(true)
        }

        "evaluate >= relation" {
            val resultObject = evaluateExpression("4 >= 4")
            resultObject shouldBe Logical(true)
        }

        "evaluate logical not" {
            val resultObject = evaluateExpression("not yes")
            resultObject shouldBe Logical(false)
        }

        "evaluate logical or" {
            val resultObject = evaluateExpression("yes or no")
            resultObject shouldBe Logical(true)
        }

        "evaluate logical and" {
            val resultObject = evaluateExpression("yes and no")
            resultObject shouldBe Logical(false)
        }

        "evaluate parenthetical expressions" {
            val resultObject = evaluateExpression("(100 + 20 + 3)")
            resultObject shouldBe (123).toRInteger()
        }

        "evaluate complex expressions" {
            val resultObject1 = evaluateExpression("(3 + 4) * 7 + #<* 1, 2 *> + (-2) ** (2 + 1)")
            resultObject1 shouldBe 43.toRInteger()
            val resultObject2 = evaluateExpression("not (not (is_text(\"Hello!\") and no)) or (4 > 3 + 2) = no")
            resultObject2 shouldBe Logical(true)
        }
    }
})
