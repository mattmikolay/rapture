package com.mattmik.rapira.visitors

import com.mattmik.rapira.objects.RObject
import java.lang.Exception

class ProcedureReturnException(val returnValue: RObject?) : Exception()
