package com.mattmik.rapira.objects

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraParser
import com.mattmik.rapira.args.Argument
import com.mattmik.rapira.args.InArgument
import com.mattmik.rapira.args.InOutArgument
import com.mattmik.rapira.control.CallableReturnException
import com.mattmik.rapira.errors.IllegalArgumentError
import com.mattmik.rapira.errors.IllegalReturnValueError
import com.mattmik.rapira.errors.IncorrectArgumentCountError
import com.mattmik.rapira.params.ParamType
import com.mattmik.rapira.params.Parameter
import com.mattmik.rapira.visitors.StatementVisitor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor

class ProcedureTest : WordSpec({

    fun makeProcedureParams(vararg paramNames: String) =
        paramNames.map { paramName ->
            Parameter(
                ParamType.InOut,
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
            val mockVariableContext = mockk<RapiraParser.VariableContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeProcedureParams("param1", "param2", "param3")
            val arguments = listOf(
                InOutArgument(mockVariableContext),
                InOutArgument(mockVariableContext)
            )
            val procedure = Procedure(null, null, params)

            shouldThrow<IncorrectArgumentCountError> {
                procedure.call(Environment(), arguments, mockk())
            }
        }

        "throw exception when param and argument type do not match" {
            val mockExpressionContext = mockk<RapiraParser.ExpressionContext> {
                start = mockk()
                stop = mockk()
            }
            val params = makeProcedureParams("param1")
            val arguments = listOf(
                InArgument(mockExpressionContext)
            )
            val procedure = Procedure(null, null, params)

            shouldThrow<IllegalArgumentError> {
                procedure.call(Environment(), arguments, mockk())
            }
        }

        "throw exception when CallableReturnException holds non-null value" {
            val params = emptyList<Parameter>()
            val arguments = emptyList<Argument>()
            val mockStatements = mockk<RapiraParser.StmtsContext>(relaxed = true)
            val returnValue = Text("Hello, world!")
            every { anyConstructed<StatementVisitor>().visit(any()) } throws CallableReturnException(
                returnValue = returnValue,
                token = mockk()
            )

            shouldThrow<IllegalReturnValueError> {
                Procedure(null, mockStatements, params).call(Environment(), arguments, mockk())
            }
        }

        "return when CallableReturnException holds null value" {
            val params = emptyList<Parameter>()
            val arguments = emptyList<Argument>()
            val mockStatements = mockk<RapiraParser.StmtsContext>(relaxed = true)
            every { anyConstructed<StatementVisitor>().visit(any()) } throws CallableReturnException(
                returnValue = null,
                token = mockk()
            )

            Procedure(null, mockStatements, params)
                .call(Environment(), arguments, mockk())
                .shouldBeNull()
        }
    }

    "toString" When {
        "procedure is named" should {
            "return user friendly representation" {
                Procedure("foo") shouldConvertToString "proc[\"foo\"]"
            }
        }

        "procedure is unnamed" should {
            "return user friendly representation" {
                Procedure() shouldConvertToString "proc"
            }
        }
    }
})
