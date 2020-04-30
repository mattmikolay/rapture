package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.RapiraFunction
import com.mattmik.rapira.objects.RapiraLogical
import com.mattmik.rapira.objects.RapiraObject
import com.mattmik.rapira.objects.RapiraProcedure
import com.mattmik.rapira.objects.toRapiraInteger
import com.mattmik.rapira.objects.toRapiraReal
import com.mattmik.rapira.objects.toRapiraSequence
import com.mattmik.rapira.objects.toRapiraText
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ExpressionVisitorTest : WordSpec({
    lateinit var environment: Environment

    fun evaluateExpression(input: String): RapiraObject? {
        val lexer = RapiraLangLexer(CharStreams.fromString(input))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.expression()
        return ExpressionVisitor(environment).visit(tree)
    }

    beforeTest {
        environment = Environment()
        environment["alpha"] = "Ready!".toRapiraText()
    }

    "visit" should {
        "evaluate variables" {
            val resultObject = evaluateExpression("alpha")
            resultObject shouldBe "Ready!".toRapiraText()
        }

        "evaluate text" {
            val resultObject = evaluateExpression("\"Hello, world!\"")
            resultObject shouldBe "Hello, world!".toRapiraText()
        }

        "evaluate unsigned integer numbers" {
            val resultObject = evaluateExpression("12345")
            resultObject shouldBe 12345.toRapiraInteger()
        }

        "evaluate unsigned real numbers" {
            val resultObject = evaluateExpression("12.345")
            resultObject shouldBe (12.345).toRapiraReal()
        }

        "evaluate procedure definitions" {
            val resultObject = evaluateExpression("""
                proc (param1, =>param2, <=param3)
                    output: "Hello, world!"
                    test := 3 + 4
                end
            """.trimIndent())
            resultObject should beOfType<RapiraProcedure>()
        }

        "evaluate function definitions" {
            val resultObject = evaluateExpression("""
                fun (param1, =>param2)
                    output: "Hello, world!"
                    test := 3 + 4
                end
            """.trimIndent())
            resultObject should beOfType<RapiraFunction>()
        }

        "evaluate sequences" {
            val resultObject = evaluateExpression(
                "<* 1, 2, 3, \"a\", \"b\", \"c\" *>"
            )
            resultObject shouldBe listOf(
                1.toRapiraInteger(),
                2.toRapiraInteger(),
                3.toRapiraInteger(),
                "a".toRapiraText(),
                "b".toRapiraText(),
                "c".toRapiraText()
            ).toRapiraSequence()
        }

        "evaluate length" {
            val resultObject = evaluateExpression("#\"Hello\"")
            resultObject shouldBe "Hello".toRapiraText().length()
        }

        "evaluate index expressions with commas" {
            val resultObject1 = evaluateExpression("<* 100, 200, 300 *>[2]")
            resultObject1 shouldBe 200.toRapiraInteger()
            val resultObject2 = evaluateExpression("<* 100, <* 2000, 3000, 4000, 5000 *>, 300 *>[2][4]")
            resultObject2 shouldBe 5000.toRapiraInteger()
        }

        "evaluate index expressions with colons" {
            val resultObject1 = evaluateExpression("<* 100, 200, 300 *>[:]")
            resultObject1 shouldBe listOf(
                100.toRapiraInteger(),
                200.toRapiraInteger(),
                300.toRapiraInteger()
            ).toRapiraSequence()
            val resultObject2 = evaluateExpression("<* 100, 200, 300 *>[2:3]")
            resultObject2 shouldBe listOf(
                200.toRapiraInteger(),
                300.toRapiraInteger()
            ).toRapiraSequence()
            val resultObject3 = evaluateExpression("<* 100, 200, 300 *>[2:]")
            resultObject3 shouldBe listOf(
                200.toRapiraInteger(),
                300.toRapiraInteger()
            ).toRapiraSequence()
            val resultObject4 = evaluateExpression("<* 100, 200, 300 *>[:2]")
            resultObject4 shouldBe listOf(
                100.toRapiraInteger(),
                200.toRapiraInteger()
            ).toRapiraSequence()
        }

        "evaluate function calls" {
            val resultObject1 = evaluateExpression("is_text(\"Hello\")")
            resultObject1 shouldBe RapiraLogical(true)
            val resultObject2 = evaluateExpression("abs(=>-123)")
            resultObject2 shouldBe 123.toRapiraInteger()
        }

        "evaluate unary positive" {
            val resultObject = evaluateExpression("+12345")
            resultObject shouldBe 12345.toRapiraInteger()
        }

        "evaluate unary negative" {
            val resultObject = evaluateExpression("-12345")
            resultObject shouldBe (-12345).toRapiraInteger()
        }

        "evaluate addition" {
            val resultObject = evaluateExpression("2 + 4")
            resultObject shouldBe 6.toRapiraInteger()
        }

        "evaluate subtraction" {
            val resultObject = evaluateExpression("50 - 25")
            resultObject shouldBe 25.toRapiraInteger()
        }

        "evaluate multiplication" {
            val resultObject = evaluateExpression("2 * 12")
            resultObject shouldBe 24.toRapiraInteger()
        }

        "evaluate division" {
            val resultObject = evaluateExpression("100 / 25")
            resultObject shouldBe 4.toRapiraInteger()
        }

        "evaluate integer division" {
            val resultObject = evaluateExpression("7 // 3")
            resultObject shouldBe 2.toRapiraInteger()
        }

        "evaluate modulo" {
            val resultObject = evaluateExpression("7 /% 3")
            resultObject shouldBe 1.toRapiraInteger()
        }

        "evaluate exponentiation" {
            val resultObject = evaluateExpression("3 ** 4")
            resultObject shouldBe 81.toRapiraInteger()
        }

        "evaluate equality" {
            val resultObject = evaluateExpression("1 = 1")
            resultObject shouldBe RapiraLogical(true)
        }

        "evaluate inequality" {
            val resultObject = evaluateExpression("1 /= 1")
            resultObject shouldBe RapiraLogical(false)
        }

        "evaluate < relation" {
            val resultObject = evaluateExpression("4 < 5")
            resultObject shouldBe RapiraLogical(true)
        }

        "evaluate > relation" {
            val resultObject = evaluateExpression("4 > 5")
            resultObject shouldBe RapiraLogical(false)
        }

        "evaluate <= relation" {
            val resultObject = evaluateExpression("4 <= 4")
            resultObject shouldBe RapiraLogical(true)
        }

        "evaluate >= relation" {
            val resultObject = evaluateExpression("4 >= 4")
            resultObject shouldBe RapiraLogical(true)
        }

        "evaluate logical not" {
            val resultObject = evaluateExpression("not yes")
            resultObject shouldBe RapiraLogical(false)
        }

        "evaluate logical or" {
            val resultObject = evaluateExpression("yes or no")
            resultObject shouldBe RapiraLogical(true)
        }

        "evaluate logical and" {
            val resultObject = evaluateExpression("yes and no")
            resultObject shouldBe RapiraLogical(false)
        }

        "evaluate parenthetical expressions" {
            val resultObject = evaluateExpression("(100 + 20 + 3)")
            resultObject shouldBe (123).toRapiraInteger()
        }

        "evaluate complex expressions" {
            val resultObject1 = evaluateExpression("(3 + 4) * 7 + #<* 1, 2 *> + (-2) ** (2 + 1)")
            resultObject1 shouldBe 43.toRapiraInteger()
            val resultObject2 = evaluateExpression("not (not (is_text(\"Hello!\") and no)) or (4 > 3 + 2) = no")
            resultObject2 shouldBe RapiraLogical(true)
        }
    }
})
