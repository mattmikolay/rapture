package com.mattmik.rapira.objects

import com.mattmik.rapira.errors.RapiraIndexOutOfBoundsError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.checkAll

class SequenceTest : WordSpec({
    "vararg constructor" should {
        "set entries list" {
            val obj1 = Text("Hello, world!")
            val obj2 = RInteger(123)
            val obj3 = LogicalYes

            val sequence = Sequence(obj1, obj2, obj3)

            sequence.entries shouldBe listOf(obj1, obj2, obj3)
        }
    }

    "times" should {
        val sequence = listOf(
            1.toRInteger(),
            "Hello, world!".toText(),
            2.toRInteger(),
            "This is a test.".toText()
        ).toSequence()

        "succeed with empty sequence when given 0" {
            sequence * RInteger(0) shouldSucceedWith Sequence(emptyList())
        }

        "succeed with sequence when given positive integer" {
            sequence * RInteger(3) shouldSucceedWith listOf(
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText(),
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText(),
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText()
            ).toSequence()
        }

        "error when given negative integer" {
            checkAll(Arb.negativeInts()) { num ->
                sequence * RInteger(num) should beOfType<OperationResult.Error>()
            }
        }
    }

    "length" should {
        "succeed with integer" {
            checkAll(Arb.list(rapiraObjectArb)) { objectList ->
                objectList.toSequence().length() shouldSucceedWith objectList.size.toRInteger()
            }
        }
    }

    "element at" should {
        "return object when given integer" {
            val sequence = listOf(
                1.toRInteger(),
                "Hello, world!".toText(),
                2.toRInteger(),
                "This is a test.".toText()
            ).toSequence()

            sequence.elementAt(1.toRInteger()) shouldBe 1.toRInteger()
            sequence.elementAt(2.toRInteger()) shouldBe "Hello, world!".toText()
            sequence.elementAt(3.toRInteger()) shouldBe 2.toRInteger()
            sequence.elementAt(4.toRInteger()) shouldBe "This is a test.".toText()

            shouldThrow<RapiraIndexOutOfBoundsError> {
                sequence.elementAt(0.toRInteger())
            }
            shouldThrow<RapiraIndexOutOfBoundsError> {
                sequence.elementAt(5.toRInteger())
            }
        }
    }

    "slice" should {
        "return object when given integer" {
            val sequence = listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger(),
                4.toRInteger()
            ).toSequence()

            sequence.slice(null, null) shouldBe sequence
            sequence.slice(2.toRInteger(), null) shouldBe listOf(
                2.toRInteger(),
                3.toRInteger(),
                4.toRInteger()
            ).toSequence()
            sequence.slice(2.toRInteger(), 3.toRInteger()) shouldBe listOf(
                2.toRInteger(),
                3.toRInteger()
            ).toSequence()
            sequence.slice(null, 3.toRInteger()) shouldBe listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger()
            ).toSequence()
        }
    }

    "toString" should {
        "return user friendly representation" {
            val emptySequence = emptyList<RObject>().toSequence()
            emptySequence shouldConvertToString "<* *>"

            val simpleNumberSequence = listOf(1, 2, 3)
                .map { num -> num.toRInteger() }
                .toSequence()
            simpleNumberSequence shouldConvertToString "<* 1, 2, 3 *>"

            val nestedSequences = listOf(
                1.toRInteger(),
                listOf(
                    2.toRInteger(),
                    3.toRInteger(),
                    4.toRInteger()
                ).toSequence(),
                5.toRInteger()
            ).toSequence()
            nestedSequences shouldConvertToString "<* 1, <* 2, 3, 4 *>, 5 *>"

            val sequenceOfMixedTypes = listOf(
                1.toRInteger(),
                Real(2.5),
                "okay".toText()
            ).toSequence()
            sequenceOfMixedTypes shouldConvertToString "<* 1, 2.5, \"okay\" *>"
        }
    }
})
