package com.mattmik.rapira.control

import com.mattmik.rapira.objects.RObject
import java.lang.Exception

class ProcedureReturnException(val returnValue: RObject?) : Exception()
