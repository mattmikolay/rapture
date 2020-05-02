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
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
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
        mockkObject(ConsoleWriter)
        environment = Environment()
        environment["alpha"] = SimpleVariable("Ready!".toText())
        environment["month"] = SimpleVariable(12.toRInteger())
        environment["animal"] = SimpleVariable(Text("cat"))
    }

    afterTest {
        unmockkObject(ConsoleWriter)
    }

    "visit" should {
        "handle procedure definitions" {
            environment["test_procedure"].value shouldBe Empty
            evaluateStatements(
                """
                    proc test_procedure (param1, =>param2, <=param3)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )
            environment["test_procedure"].value should beOfType<Procedure>()
        }

        "handle function definitions" {
            environment["test_function"].value shouldBe Empty
            evaluateStatements(
                """
                    fun test_function (param1, =>param2)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )
            environment["test_function"].value should beOfType<Function>()
        }

        "handle assignment statements" {
            environment["str_value"].value shouldBe Empty
            evaluateStatements("str_value := 3 * alpha")
            environment["str_value"].value shouldBe Text("Ready!Ready!Ready!")

            // TODO Test index expression assignment
        }

        // TODO Test call statements

        "handle if statements without else clauses" {
            environment["season"].value shouldBe Empty
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    if month = 12 then season := "winter"
                    fi
                    if animal = "dog" then sound := "bark"
                    fi
                """.trimIndent()
            )

            environment["season"].value shouldBe Text("winter")
            environment["sound"].value shouldBe Empty
        }

        "handle if statements with else clauses" {
            environment["season"].value shouldBe Empty
            environment["sound"].value shouldBe Empty

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

            environment["season"].value shouldBe Text("winter")
            environment["sound"].value shouldBe Text("meow")
        }

        "handle condition case statements 1" {
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].value shouldBe Text("meow")
        }

        "handle condition case statements 2" {
            environment["animal"].value = Text("hedgehog")
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].value shouldBe Text("moo")
        }

        "handle condition case statements 3" {
            environment["animal"].value = Text("seal")
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].value shouldBe Text("bark")
        }

        "handle conditionless case statements 1" {
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    case when animal = "dog": sound := "bark"
                        when animal = "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].value shouldBe Text("meow")
        }

        "handle conditionless case statements 2" {
            environment["animal"].value = Text("hedgehog")
            environment["sound"].value shouldBe Empty

            evaluateStatements(
                """
                    case when animal = "dog": sound := "bark"
                        when animal = "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].value shouldBe Text("moo")
        }

        // TODO
        "handle output statements with line break" {
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

        "handle expression statements" {
            val statement = "3 * alpha"

            val lexer = RapiraLangLexer(CharStreams.fromString("$statement\n"))
            val parser = RapiraLangParser(CommonTokenStream(lexer))
            val tree = parser.dialogUnit()
            StatementVisitor(environment).visit(tree)

            verify {
                ConsoleWriter.println(Text("Ready!Ready!Ready!").toString())
            }
        }

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
