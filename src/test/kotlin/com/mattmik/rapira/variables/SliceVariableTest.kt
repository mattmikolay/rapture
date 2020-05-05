package com.mattmik.rapira.variables

import com.mattmik.rapira.objects.RInteger
import com.mattmik.rapira.objects.Sequence
import com.mattmik.rapira.objects.Text
import com.mattmik.rapira.objects.toSequence
import com.mattmik.rapira.objects.toText
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SliceVariableTest : WordSpec({

    "get" When {
        "base variable is text" should {
            val textObject = "Hello, world!".toText()
            val textVariable = SimpleVariable(textObject)

            "return slice at specified indexes" {
                val sliceVariable = SliceVariable(
                    textVariable,
                    RInteger(4),
                    RInteger(8)
                )
                sliceVariable.value shouldBe Text("lo, w")
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

            "return slice at specified indexes" {
                val sliceVariable = SliceVariable(
                    sequenceVariable,
                    RInteger(2),
                    RInteger(3)
                )
                sliceVariable.value shouldBe listOf(
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
                sliceVariable.value shouldBe Text("lo, w")

                sliceVariable.value = Text("p me escape from this w")

                sliceVariable.value shouldBe Text("p me ")
                textVariable.value shouldBe Text("Help me escape from this world!")
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
                sliceVariable.value shouldBe Sequence(
                    Text("beta"),
                    Text("gamma")
                )

                sliceVariable.value = Sequence(Text("epsilon"))

                sliceVariable.value shouldBe Sequence(
                    Text("epsilon"),
                    Text("delta")
                )
                sequenceVariable.value shouldBe Sequence(
                    Text("alpha"),
                    Text("epsilon"),
                    Text("delta")
                )
            }
        }
    }
})
