package com.github.timofeyGusev.core

import tofu.optics.macros.ClassyOptics

@ClassyOptics
final case class RocksDBSettings(path: String)
