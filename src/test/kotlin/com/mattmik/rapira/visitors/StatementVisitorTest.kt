package com.mattmik.rapira.visitors

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class StatementVisitorTest : WordSpec({
    lateinit var environment: Environment

    fun evaluateStatements(input: String) {
        val lexer = RapiraLangLexer(CharStreams.fromString("$input\n"))
        val parser = RapiraLangParser(CommonTokenStream(lexer))
        val tree = parser.fileInput()
        StatementVisitor(environment).visit(tree)
    }

    beforeTest {
        environment = Environment()
        environment["alpha"] = "Ready!".toText()
    }

    "visit" should {
        "handle procedure definitions" {
            environment["test_procedure"] shouldBe Empty
            evaluateStatements(
                """
                    proc test_procedure (param1, =>param2, <=param3)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )
            environment["test_procedure"] should beOfType<Procedure>()
        }

        "handle function definitions" {
            environment["test_function"] shouldBe Empty
            evaluateStatements(
                """
                    fun test_function (param1, =>param2)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )
            environment["test_function"] should beOfType<Function>()
        }

        "handle assignment statements" {
            environment["int_value"] shouldBe Empty
            evaluateStatements("int_value := 3 * alpha")
            environment["int_value"] shouldBe Text("Ready!Ready!Ready!")

            // TODO Test index expression assignment
        }
    }
})
