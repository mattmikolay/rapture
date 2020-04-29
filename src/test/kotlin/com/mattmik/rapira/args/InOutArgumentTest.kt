package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.toRapiraText
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

private const val VARIABLE_NAME = "hello_world"

class InOutArgumentTest : WordSpec() {
    private lateinit var environment: Environment

    init {
        beforeTest {
            environment = Environment()
            environment[VARIABLE_NAME] = "Hello, world!".toRapiraText()
        }
        "evaluate" should {
            "return value of simple identifier in environment" {
                val variableValue = "Hello, world!".toRapiraText()
                val lexer = RapiraLangLexer(CharStreams.fromString(VARIABLE_NAME))
                val parser = RapiraLangParser(CommonTokenStream(lexer))
                val tree = parser.variable()

                val argument = InOutArgument(tree)
                val evaluationResult = argument.evaluate(environment)

                evaluationResult shouldBe variableValue
            }
        }
    }
}
