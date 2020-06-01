package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

private const val VARIABLE_NAME = "hello_world"

class InOutArgumentTest : WordSpec() {
    private lateinit var environment: Environment

    init {
        beforeTest {
            environment = Environment()
            environment[VARIABLE_NAME].setValue("Hello, world!".toText())
        }
        "evaluate" should {
            "return variable in environment" {
                val expectedVariable = environment[VARIABLE_NAME]
                val expectedObject = "Hello, world!".toText()
                val lexer = RapiraLexer(CharStreams.fromString(VARIABLE_NAME))
                val parser = RapiraParser(CommonTokenStream(lexer))
                val tree = parser.variable()

                val argument = InOutArgument(tree)
                val actualVariable = argument.evaluate(environment)

                actualVariable shouldBeSameInstanceAs expectedVariable
                actualVariable.getValue() shouldSucceedWith expectedObject
            }
        }
    }
}
