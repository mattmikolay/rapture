package com.mattmik.rapira.visitors

import com.mattmik.rapira.objects.RapiraObject
import java.lang.Exception

class ProcedureReturnException(val returnValue: RapiraObject?) : Exception()
