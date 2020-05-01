package com.mattmik.rapira.visitors

import com.mattmik.rapira.ConsoleWriter
import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangLexer
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.control.ProcedureReturnException
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toText
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.mockkObject
import io.mockk.verifyOrder
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
        environment["month"] = 12.toRInteger()
        environment["animal"] = Text("cat")
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

        // TODO Test call statements

        "handle if statements without else clauses" {
            environment["season"] shouldBe Empty
            environment["sound"] shouldBe Empty

            evaluateStatements(
                """
                    if month = 12 then season := "winter"
                    fi
                    if animal = "dog" then sound := "bark"
                    fi
                """.trimIndent()
            )

            environment["season"] shouldBe Text("winter")
            environment["sound"] shouldBe Empty
        }

        "handle if statements with else clauses" {
            environment["season"] shouldBe Empty
            environment["sound"] shouldBe Empty

            evaluateStatements(
                """
                    if month = 12 then season := "winter"
                    else season := "summer"
                    fi

                    if animal = "dog" then sound := "bark"
                    else sound := "meow"
                    fi
                """.trimIndent()
            )

            environment["season"] shouldBe Text("winter")
            environment["sound"] shouldBe Text("meow")
        }

        // TODO
        "handle output statements with line break" {
            mockkObject(ConsoleWriter)
            evaluateStatements(
                """
                    output
                    output: alpha
                    output: "The dog chases the", animal
                """.trimIndent()
            )
            verifyOrder {
                ConsoleWriter.printObjects(
                    objects = emptyList(),
                    lineBreak = true
                )
                ConsoleWriter.printObjects(
                    objects = listOf(Text("Ready!")),
                    lineBreak = true
                )
                ConsoleWriter.printObjects(
                    objects = listOf(
                        Text("The dog chases the"),
                        Text("cat")
                    ),
                    lineBreak = true
                )
            }
        }

        "handle output statements without line break" {
            mockkObject(ConsoleWriter)
            evaluateStatements(
                """
                    output nlf
                    output nlf: alpha
                    output nlf: "The dog chases the", animal
                """.trimIndent()
            )
            verifyOrder {
                ConsoleWriter.printObjects(
                    objects = emptyList(),
                    lineBreak = false
                )
                ConsoleWriter.printObjects(
                    objects = listOf(Text("Ready!")),
                    lineBreak = false
                )
                ConsoleWriter.printObjects(
                    objects = listOf(
                        Text("The dog chases the"),
                        Text("cat")
                    ),
                    lineBreak = false
                )
            }
        }

        // TODO

        "throw exception when exit statement occurs outside loop" {
            shouldThrow<LoopExitException> {
                evaluateStatements("exit")
            }
        }

        "throw exception when return statement occurs outside procedure" {
            shouldThrow<ProcedureReturnException> {
                evaluateStatements("return")
            }
        }
    }
})
