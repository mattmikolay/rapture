package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.shouldSucceedWith
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec

class SliceVariableTest : WordSpec({

    "get" When {
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

    "set" When {
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

                sliceVariable.setValue(Text("p me escape from this w")) shouldSucceedWith Text("Help me escape from this world!")

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

                sliceVariable.setValue(Sequence(Text("epsilon"))) shouldSucceedWith Sequence(
                    Text("alpha"),
                    Text("epsilon"),
                    Text("delta")
                )

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
    }
})
