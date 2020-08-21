package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.Empty
import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldError
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec

class SliceVariableTest : WordSpec({

    "getValue" When {
        "base variable is text" should {
            val textObject = "Hello, world!".toText()
            val textVariable = SimpleVariable(textObject)

            "succeed with slice at specified indexes" {
                val sliceVariable = SliceVariable(
                    textVariable,
                    RInteger(4),
                    RInteger(8)
                )
                sliceVariable.getValue() shouldSucceedWith Text("lo, w")
            }
        }

        "base variable is sequence" should {
            val sequenceObject = listOf(
                Text("alpha"),
                Text("beta"),
                Text("gamma"),
                Text("delta")
            ).toSequence()
            val sequenceVariable = SimpleVariable(sequenceObject)

            "succeed with slice at specified indexes" {
                val sliceVariable = SliceVariable(
                    sequenceVariable,
                    RInteger(2),
                    RInteger(3)
                )
                sliceVariable.getValue() shouldSucceedWith listOf(
                    Text("beta"),
                    Text("gamma")
                ).toSequence()
            }
        }
    }

    "setValue" When {
        "base variable is text" should {
            val textObject = "Hello, world!".toText()
            val textVariable = SimpleVariable(textObject)

            "replace substring" {
                val sliceVariable = SliceVariable(
                    textVariable,
                    RInteger(4),
                    RInteger(8)
                )
                sliceVariable.getValue() shouldSucceedWith Text("lo, w")

                sliceVariable.setValue(Text("p me escape from this w")) shouldSucceedWith Unit

                sliceVariable.getValue() shouldSucceedWith Text("p me ")
                textVariable.getValue() shouldSucceedWith Text("Help me escape from this world!")
            }
        }

        "base variable is sequence" should {
            val sequenceObject = listOf(
                Text("alpha"),
                Text("beta"),
                Text("gamma"),
                Text("delta")
            ).toSequence()
            val sequenceVariable = SimpleVariable(sequenceObject)

            "replace subsequence" {
                val sliceVariable = SliceVariable(
                    sequenceVariable,
                    RInteger(2),
                    RInteger(3)
                )
                sliceVariable.getValue() shouldSucceedWith Sequence(
                    Text("beta"),
                    Text("gamma")
                )

                sliceVariable.setValue(Sequence(Text("epsilon"))) shouldSucceedWith Unit

                sliceVariable.getValue() shouldSucceedWith Sequence(
                    Text("epsilon"),
                    Text("delta")
                )
                sequenceVariable.getValue() shouldSucceedWith Sequence(
                    Text("alpha"),
                    Text("epsilon"),
                    Text("delta")
                )
            }
        }

        "start index is non-integer" should {
            val textVariable = SimpleVariable(value = "Hello, world!".toText())

            "return error" {
                val sliceVariable = SliceVariable(
                    textVariable,
                    startIndex = Empty,
                    endIndex = RInteger(2)
                )

                sliceVariable.setValue("test".toText()).shouldError()
            }
        }

        "end index is non-integer" should {
            val textVariable = SimpleVariable(value = "Hello, world!".toText())

            "return error" {
                val sliceVariable = SliceVariable(
                    textVariable,
                    startIndex = RInteger(2),
                    endIndex = Empty
                )

                sliceVariable.setValue("test".toText()).shouldError()
            }
        }
    }
})
