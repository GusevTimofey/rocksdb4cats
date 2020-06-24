package com.github.gusev.core

import org.specs2.mutable.Specification

class RDBTests extends Specification {
  sequential

  "RDB should" >> {
    "remove element" >> { true mustEqual true }
    "remove elements" >> { true mustEqual true }
    "insert element" >> { true mustEqual true }
    "insert elements" >> { true mustEqual true }
    "get element should" >> {
      "not return element if it doesn't exist" >> {
        true mustEqual true
      }
      "return element if it exists" >> { true mustEqual true }
    }
    "get elements" >> { true mustEqual true }
  }
}
