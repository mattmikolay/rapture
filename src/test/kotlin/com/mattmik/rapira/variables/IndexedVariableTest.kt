package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldError
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec

class IndexedVariableTest : WordSpec({
    "get" should {
        "succeed with element at specified index" {
            val sequence = Sequence(
                Text("alpha"),
                Text("beta"),
                Text("gamma")
            )
            val simpleVariable = SimpleVariable(sequence)
            val indexedVariable = IndexedVariable(simpleVariable, RInteger(2))
            indexedVariable.getValue() shouldSucceedWith Text("beta")
        }
    }

    "set" When {
        "base variable is text" should {
            val textObject = "Hello, world!".toText()
            val textVariable = SimpleVariable(textObject)

            "update character at specified index" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))

                indexedVariable.getValue() shouldSucceedWith Text("e")
                indexedVariable.setValue(Text("a")) shouldSucceedWith Unit
                indexedVariable.getValue() shouldSucceedWith Text("a")
                textVariable.getValue() shouldSucceedWith "Hallo, world!".toText()
            }

            "error if index is non-text" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))
                indexedVariable.setValue(Empty).shouldError()
            }

            "error if index is text with invalid length" {
                val indexedVariable = IndexedVariable(textVariable, RInteger(2))
                indexedVariable.setValue(Text("hello")).shouldError()
            }
        }

        "base variable is sequence" should {
            val sequenceObject = Sequence(
                Text("alpha"),
                Text("beta"),
                Text("gamma")
            )
            val sequenceVariable = SimpleVariable(sequenceObject)

            "update element at specified index" {
                val indexedVariable = IndexedVariable(sequenceVariable, RInteger(2))

                indexedVariable.getValue() shouldSucceedWith Text("beta")
                indexedVariable.setValue(Text("delta")) shouldSucceedWith Unit
                indexedVariable.getValue() shouldSucceedWith Text("delta")
                sequenceVariable.getValue() shouldSucceedWith Sequence(
                    Text("alpha"),
                    Text("delta"),
                    Text("gamma")
                )
            }
        }
    }
})
