package com.mattmik.rapira.variables

import com.mattmik.rapira.errors.RapiraInvalidOperationError
import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import io.kotest.assertions.throwables.shouldThrowUnit
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

    "set" When {
        "base variable is text" should {
            val textObject = "Hello, world!".toText()
            val textVariable = SimpleVariable(textObject)

            "update character at specified index" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))

                indexedVariable.value shouldBe Text("e")
                indexedVariable.value = Text("a")
                indexedVariable.value shouldBe Text("a")
                textVariable.value shouldBe "Hallo, world!".toText()
            }

            "throw error if index is non-text" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))
                shouldThrowUnit<RapiraInvalidOperationError> {
                    indexedVariable.value = Empty
                }
            }

            "throw error if index is text with invalid length" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))
                shouldThrowUnit<RapiraInvalidOperationError> {
                    indexedVariable.value = Text("hello")
                }
            }
        }

        "base variable is sequence" should {
            val sequenceObject = listOf(
                Text("alpha"),
                Text("beta"),
                Text("gamma")
            ).toSequence()
            val sequenceVariable = SimpleVariable(sequenceObject)

            "update element at specified index" {
                val indexedVariable = IndexedVariable(sequenceVariable, RInteger(2))

                indexedVariable.value shouldBe Text("beta")
                indexedVariable.value = Text("delta")
                indexedVariable.value shouldBe Text("delta")
                sequenceVariable.value shouldBe listOf(
                    Text("alpha"),
                    Text("delta"),
                    Text("gamma")
                ).toSequence()
            }
        }
    }
})
