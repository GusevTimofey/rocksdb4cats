package com.github.gusev.core

import tofu.optics.macros.ClassyOptics

@ClassyOptics
final case class RocksDBSettings(path: String)
