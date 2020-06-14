package com.mattmik.rapira.visitors

import com.mattmik.rapira.CONST_YES
import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLexer
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.console.ConsoleReader
import com.mattmik.rapira.console.ConsoleWriter
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.control.LoopExitException
import com.mattmik.rapira.errors.InvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.Function
import com.mattmik.rapira.objects.Procedure
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import com.mattmik.rapira.util.Result
import com.mattmik.rapira.variables.SimpleVariable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.mockk.Called
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class StatementVisitorTest : WordSpec({
    lateinit var environment: Environment

    fun evaluateStatements(input: String) {
        val lexer = RapiraLexer(CharStreams.fromString(input))
        val parser = RapiraParser(CommonTokenStream(lexer))
        val tree = parser.fileInput()
        StatementVisitor(environment).visit(tree)
    }

    beforeTest {
        mockkObject(ConsoleWriter)
        mockkObject(ConsoleReader)
        environment = Environment()
        environment["alpha"] = SimpleVariable("Ready!".toText())
        environment["month"] = SimpleVariable(12.toRInteger())
        environment["animal"] = SimpleVariable(Text("cat"))
        environment["weather_types"] = SimpleVariable(
            Sequence(
                "sunny".toText(),
                "rainy".toText()
            )
        )
    }

    afterTest {
        unmockkObject(ConsoleReader)
        unmockkObject(ConsoleWriter)
    }

    "visit" should {
        "handle procedure definitions" {
            environment["test_procedure"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    proc test_procedure (param1, =>param2, <=param3)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )

            environment["test_procedure"].getValue() should beOfType<Result.Success<Procedure>>()
            verify {
                ConsoleWriter wasNot Called
            }
        }

        "handle function definitions" {
            environment["test_function"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    fun test_function (param1, =>param2)
                        output: "Hello, world!"
                    end
                """.trimIndent()
            )

            environment["test_function"].getValue() should beOfType<Result.Success<Function>>()
            verify {
                ConsoleWriter wasNot Called
            }
        }

        "handle assignment statements" {
            environment["str_value"].getValue() shouldSucceedWith Empty
            evaluateStatements(
                """
                    str_value := 3 * alpha
                    weather_types[1 + 1] := "snowy"
                """.trimIndent()
            )
            environment["str_value"].getValue() shouldSucceedWith Text("Ready!Ready!Ready!")
            environment["weather_types"].getValue() shouldSucceedWith Sequence(
                Text("sunny"),
                Text("snowy")
            )
        }

        "handle call statement with procedures" {
            evaluateStatements(
                """
                    proc test_procedure (param1, =>param2, <=param3)
                        output: "Hello, world!", param1, param2, param3
                    end
                    call test_procedure(123, =>alpha, <=month)
                """.trimIndent()
            )
            verify {
                ConsoleWriter.printObjects(
                    listOf(
                        Text("Hello, world!"),
                        RInteger(123),
                        Text("Ready!"),
                        RInteger(12)
                    ),
                    lineBreak = true
                )
            }
        }

        "handle call statement with functions" {
            evaluateStatements(
                """
                    fun test_function (param1, =>param2)
                        output: "Hello, world!", param1, param2
                    end
                    call test_function(123, alpha)
                """.trimIndent()
            )
            verify {
                ConsoleWriter.printObjects(
                    listOf(
                        Text("Hello, world!"),
                        RInteger(123),
                        Text("Ready!")
                    ),
                    lineBreak = true
                )
            }
        }

        "handle recursive calls" {
            evaluateStatements(
                """
                    fun sayHello(n)
                        output: "Hello, world!"
                        if n > 1 then
                            sayHello(n - 1)
                        fi
                    end
                    call sayHello(3)
                """.trimIndent()
            )
            verify(exactly = 3) {
                ConsoleWriter.printObjects(
                    listOf(
                        Text("Hello, world!")
                    ),
                    lineBreak = true
                )
            }
        }

        "handle if statements without else clauses" {
            environment["season"].getValue() shouldSucceedWith Empty
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    if month = 12 then
                        season := "winter"
                    fi
                    if animal = "dog" then
                        sound := "bark"
                    fi
                """.trimIndent()
            )

            environment["season"].getValue() shouldSucceedWith Text("winter")
            environment["sound"].getValue() shouldSucceedWith Empty
        }

        "handle if statements with else clauses" {
            environment["season"].getValue() shouldSucceedWith Empty
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    if month = 12 then
                        season := "winter"
                    else
                        season := "summer"
                    fi

                    if animal = "dog" then
                        sound := "bark"
                    else
                        sound := "meow"
                    fi
                """.trimIndent()
            )

            environment["season"].getValue() shouldSucceedWith Text("winter")
            environment["sound"].getValue() shouldSucceedWith Text("meow")
        }

        "handle condition case statements 1" {
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].getValue() shouldSucceedWith Text("meow")
        }

        "handle condition case statements 2" {
            environment["animal"].setValue(Text("hedgehog"))
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].getValue() shouldSucceedWith Text("moo")
        }

        "handle condition case statements 3" {
            environment["animal"].setValue(Text("seal"))
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    case animal when "dog", "seal": sound := "bark"
                        when "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].getValue() shouldSucceedWith Text("bark")
        }

        "handle conditionless case statements 1" {
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    case when animal = "dog": sound := "bark"
                        when animal = "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].getValue() shouldSucceedWith Text("meow")
        }

        "handle conditionless case statements 2" {
            environment["animal"].setValue(Text("hedgehog"))
            environment["sound"].getValue() shouldSucceedWith Empty

            evaluateStatements(
                """
                    case when animal = "dog": sound := "bark"
                        when animal = "cat": sound := "meow"
                        else sound := "moo"
                    esac
                """.trimIndent()
            )

            environment["sound"].getValue() shouldSucceedWith Text("moo")
        }

        "handle do loops with until" {
            evaluateStatements(
                """
                    num := 1
                    do
                        output: num
                        num := num + 1
                    until num = 4
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(1)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(2)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle while loops" {
            evaluateStatements(
                """
                    num := 1
                    while num < 4 do
                        output: num
                        num := num + 1
                    od
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(1)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(2)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle while loops with until" {
            evaluateStatements(
                """
                    num := 1
                    while num < 10 do
                        output: num
                        num := num + 2
                    until num /% 5 = 0
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(1)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle repeat loops" {
            evaluateStatements(
                """
                    repeat 3 do
                        output: "Hello, world!"
                    od
                """.trimIndent()
            )
            verify(exactly = 3) {
                ConsoleWriter.printObjects(
                    objects = listOf(
                        Text("Hello, world!")
                    ),
                    lineBreak = true
                )
            }
        }

        "handle repeat loop with while and until" {
            evaluateStatements(
                """
                    num := 1
                    repeat 2 while num < 10 do
                        output: num
                        num := num + 2
                    until num /% 7 = 0
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(1)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle for loops with from, to, and positive step" {
            evaluateStatements(
                """
                    for i from 3 to 13 step 4 do
                        output: i
                    od
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(7)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(11)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle for loops with from, to, and negative step" {
            evaluateStatements(
                """
                    for i from 13 to 3 step -4 do
                        output: i
                    od
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(13)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(9)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(5)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle for loops with from and to" {
            evaluateStatements(
                """
                    for i from 3 to 5 do
                        output: i
                    od
                """.trimIndent()
            )
            verifySequence {
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(3)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(4)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
                ConsoleWriter.printObjects(
                    objects = listOf(RInteger(5)),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

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

        "handle input statements in text mode" {
            every { ConsoleReader.readText() } returnsMany
                listOf("Go!", "dog", "snowy").map { it.toText() }

            evaluateStatements(
                """
                    input text: alpha
                    input text: animal, weather_types[2]
                """.trimIndent()
            )

            verify(exactly = 3) {
                ConsoleReader.readText()
            }

            environment["alpha"].getValue() shouldSucceedWith Text("Go!")
            environment["animal"].getValue() shouldSucceedWith Text("dog")
            environment["weather_types"].getValue() shouldSucceedWith listOf(
                Text("sunny"),
                Text("snowy")
            ).toSequence()
        }

        "handle input statements in object mode" {
            every { ConsoleReader.readObject() } returnsMany
                listOf(
                    CONST_YES,
                    Text("dog"),
                    RInteger(123)
                )

            evaluateStatements(
                """
                    input: alpha
                    input: animal, weather_types[2]
                """.trimIndent()
            )

            verify(exactly = 3) {
                ConsoleReader.readObject()
            }

            environment["alpha"].getValue() shouldSucceedWith CONST_YES
            environment["animal"].getValue() shouldSucceedWith Text("dog")
            environment["weather_types"].getValue() shouldSucceedWith listOf(
                Text("sunny"),
                RInteger(123)
            ).toSequence()
        }

        "handle exit statements within loops" {
            evaluateStatements(
                """
                    do
                        output: "Hello, world!"
                        exit
                        output: "Uh oh."
                    od
                """.trimIndent()
            )
            verifyAll {
                ConsoleWriter.printObjects(
                    objects = listOf(
                        Text("Hello, world!")
                    ),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle return statements within procedures" {
            evaluateStatements(
                """
                    proc test_procedure ()
                        output: "Hello, world!"
                        return
                        output: "Uh oh."
                    end
                    call test_procedure()
                """.trimIndent()
            )
            verifyAll {
                ConsoleWriter.printObjects(
                    listOf(Text("Hello, world!")),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle return statements within functions" {
            evaluateStatements(
                """
                    fun test_function ()
                        return "Hello, world!"
                        output: "Uh oh."
                    end
                    output: test_function()
                """.trimIndent()
            )
            verifyAll {
                ConsoleWriter.printObjects(
                    listOf(Text("Hello, world!")),
                    lineBreak = true
                )
                ConsoleWriter.formatObject(any())
            }
        }

        "handle expression statements" {
            val statement = "3 * alpha"

            val lexer = RapiraLexer(CharStreams.fromString("$statement\n"))
            val parser = RapiraParser(CommonTokenStream(lexer))
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
            shouldThrow<CallableReturnException> {
                evaluateStatements("return")
            }
        }

        "throw exception when return with value occurs in procedure" {
            shouldThrow<InvalidOperationError> {
                evaluateStatements(
                    """
                        proc test_procedure ()
                            return 123
                        end
                        call test_procedure()
                    """.trimIndent()
                )
            }
        }
    }
})
