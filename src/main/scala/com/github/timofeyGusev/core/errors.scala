package com.github.timofeyGusev.core

object errors {

  sealed trait CoreError extends Throwable
  case object RDBOpErr extends CoreError
}
