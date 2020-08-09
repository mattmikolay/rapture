package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.visitors.StatementVisitor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor

class FunctionTest : WordSpec({

    fun makeFunctionParams(vararg paramNames: String) =
        paramNames.map { paramName ->
            Parameter(
                ParamType.In,
                paramName
            )
        }

    "call" should {
        beforeTest {
            mockkConstructor(StatementVisitor::class)
        }

        afterTest {
            unmockkConstructor(StatementVisitor::class)
        }

        "throw exception when param and argument count differ" {
            val params = makeFunctionParams("param1", "param2", "param3")
            val arguments = listOf(
                InArgument(mockk()),
                InArgument(mockk())
            )
            val function = Function(null, null, params)
            shouldThrow<IncorrectArgumentCountError> {
                function.call(Environment(), arguments, mockk())
            }
        }

        "throw exception when given in-out arguments" {
            val mockVariableContext = mockk<RapiraParser.VariableContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeFunctionParams("param1")
            val arguments = listOf(
                InOutArgument(mockVariableContext)
            )
            val function = Function(null, null, params)

            shouldThrow<IllegalArgumentError> {
                function.call(Environment(), arguments, mockk())
            }
        }

        "return value held by CallableReturnException" {
            val params = emptyList<Parameter>()
            val arguments = emptyList<Argument>()
            val mockStatements = mockk<RapiraParser.StmtsContext>(relaxed = true)
            val expectedReturnValue = Text("Hello, world!")
            every { anyConstructed<StatementVisitor>().visit(any()) } throws CallableReturnException(
                returnValue = expectedReturnValue,
                token = mockk()
            )

            Function(null, mockStatements, params)
                .call(Environment(), arguments, mockk()) shouldBe expectedReturnValue
        }
    }

    "toString" should {
        "return user friendly representation" {
            Function() shouldConvertToString "function"
        }
    }
})
