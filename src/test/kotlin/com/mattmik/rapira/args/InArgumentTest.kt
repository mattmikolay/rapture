package com.mattmik.rapira.args

import com.mattmik.rapira.Environment
import com.mattmik.rapira.antlr.RapiraLangParser
import com.mattmik.rapira.objects.toRInteger
import com.mattmik.rapira.visitors.ExpressionVisitor
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify

class InArgumentTest : WordSpec({
    "evaluate" should {
        "visit expression" {
            val environment = Environment()
            val mockExpressionContext = mockk<RapiraLangParser.ExpressionContext>()
            val expectedResult = 123.toRInteger()
            mockkConstructor(ExpressionVisitor::class)
            every { anyConstructed<ExpressionVisitor>().visit(any()) } returns expectedResult

            val argument = InArgument(mockExpressionContext)
            val actualResult = argument.evaluate(environment)

            actualResult shouldBe expectedResult
            verify {
                anyConstructed<ExpressionVisitor>().visit(mockExpressionContext)
            }
        }
    }
})
