package com.mattmik.rapira.objects

import com.mattmik.rapira.CONST_YES
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
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
            val obj3 = CONST_YES

            val sequence = Sequence(obj1, obj2, obj3)

            sequence.entries shouldBe listOf(obj1, obj2, obj3)
        }
    }

    "plus" should {
        val sequence = listOf(
            1.toRInteger(),
            "Hello, world!".toText(),
            2.toRInteger(),
            "This is a test.".toText()
        ).toSequence()

        "succeed with sequence when given sequence" {
            sequence + sequence shouldSucceedWith Sequence(sequence.entries + sequence.entries)
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(RInteger(1)),
                row(CONST_YES),
                row(Real(1.0)),
                row(Text("Hello, world!"))
            ) { obj ->
                (sequence + obj).shouldError()
            }
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
                (sequence * RInteger(num)).shouldError()
            }
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                (sequence * obj).shouldError()
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
        val sequence = listOf(
            1.toRInteger(),
            "Hello, world!".toText(),
            2.toRInteger(),
            "This is a test.".toText()
        ).toSequence()

        "succeed with object when given valid integer" {
            sequence.elementAt(1.toRInteger()) shouldSucceedWith 1.toRInteger()
            sequence.elementAt(2.toRInteger()) shouldSucceedWith "Hello, world!".toText()
            sequence.elementAt(3.toRInteger()) shouldSucceedWith 2.toRInteger()
            sequence.elementAt(4.toRInteger()) shouldSucceedWith "This is a test.".toText()
        }

        "error when given out of bounds integer" {
            sequence.elementAt(0.toRInteger()).shouldError()
            sequence.elementAt(5.toRInteger()).shouldError()
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                sequence.elementAt(obj).shouldError()
            }
        }
    }

    "slice" should {
        "succeed with object when given integer" {
            val sequence = listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger(),
                4.toRInteger()
            ).toSequence()

            sequence.slice(null, null) shouldSucceedWith sequence
            sequence.slice(2.toRInteger(), null) shouldSucceedWith listOf(
                2.toRInteger(),
                3.toRInteger(),
                4.toRInteger()
            ).toSequence()
            sequence.slice(2.toRInteger(), 3.toRInteger()) shouldSucceedWith listOf(
                2.toRInteger(),
                3.toRInteger()
            ).toSequence()
            sequence.slice(null, 3.toRInteger()) shouldSucceedWith listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger()
            ).toSequence()

            sequence.slice(1.toRInteger(), 0.toRInteger()) shouldSucceedWith emptyList<RObject>().toSequence()
            sequence.slice(5.toRInteger(), 4.toRInteger()) shouldSucceedWith emptyList<RObject>().toSequence()
        }

        "error when start index is out of bounds" {
            val sequence = listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger(),
            ).toSequence()
            sequence.slice(0.toRInteger(), null).shouldError()
            sequence.slice((-1).toRInteger(), null).shouldError()
        }

        "error when end index is out of bounds" {
            val sequence = listOf(
                1.toRInteger(),
                2.toRInteger(),
                3.toRInteger(),
            ).toSequence()
            sequence.slice(null, 4.toRInteger()).shouldError()
        }

        "error when given other types" {
            forAll(
                row(Empty),
                row(Procedure()),
                row(Function()),
                row(Text("Hello, world!")),
                row(CONST_YES),
                row(Real(1.0)),
                row(Sequence())
            ) { obj ->
                val testSequence = listOf(
                    1.toRInteger(),
                    "Hello, world!".toText(),
                    2.toRInteger(),
                    "This is a test.".toText()
                ).toSequence()

                testSequence.slice(null, obj).shouldError()
                testSequence.slice(obj, null).shouldError()
                testSequence.slice(obj, obj).shouldError()
            }
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
