package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class IndexedVariableTest : WordSpec({
    "get" should {
        "return element at specified index" {
            val sequence = listOf(
                Text("alpha"),
                Text("beta"),
                Text("gamma")
            ).toSequence()
            val simpleVariable = SimpleVariable(sequence)
            val indexedVariable = IndexedVariable(simpleVariable, RInteger(2))
            indexedVariable.value shouldBe Text("beta")
        }
    }

    "set" should {
        "update element at specified index" {
            val sequence = listOf(
                Text("alpha"),
                Text("beta"),
                Text("gamma")
            ).toSequence()
            val simpleVariable = SimpleVariable(sequence)
            val indexedVariable = IndexedVariable(simpleVariable, RInteger(2))

            indexedVariable.value shouldBe Text("beta")
            indexedVariable.value = Text("delta")
            indexedVariable.value shouldBe Text("delta")
            simpleVariable.value shouldBe listOf(
                Text("alpha"),
                Text("delta"),
                Text("gamma")
            ).toSequence()
        }
    }
})
