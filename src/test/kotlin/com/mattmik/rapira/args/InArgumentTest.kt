package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.visitors.ExpressionVisitor
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import io.mockk.verify

class InArgumentTest : WordSpec({
    "evaluate" should {
        beforeTest {
            mockkConstructor(ExpressionVisitor::class)
        }

        afterTest {
            unmockkConstructor(ExpressionVisitor::class)
        }

        "visit expression" {
            val environment = Environment()
            val mockExpressionContext = mockk<RapiraLangParser.ExpressionContext>()
            val expectedResult = 123.toRInteger()
            every { anyConstructed<ExpressionVisitor>().visit(any()) } returns expectedResult

            val argument = InArgument(mockExpressionContext)
            val actualResult = argument.evaluate(environment)

            actualResult.getValue() shouldSucceedWith expectedResult
            verify {
                anyConstructed<ExpressionVisitor>().visit(mockExpressionContext)
            }
        }
    }
})
