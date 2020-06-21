package com.github.timofeyGusev

import io.estatico.newtype.macros.newtype

package object core {

  @newtype final case class DBKey(value: Array[Byte]) {
    def asString: String = new String(value)
  }

  @newtype final case class DBValue(value: Array[Byte]) {
    def asString: String = new String(value)
  }

  @newtype final case class TraceId(value: String)
}
