package com.mattmik.rapira.errors

class RapiraIndexOutOfBoundsError(index: Int) : RapiraRuntimeError("Index $index is out of bounds")
