package com.github.gusev.core

object errors {

  sealed trait CoreError extends Throwable
  case object RDBOpErr extends CoreError
}
