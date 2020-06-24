package com.github.gusev

import java.io.File
import java.nio.file._
import scala.util.Random

package object core {

  def getTemporaryDir: File = {
    val dir: File = Files
      .createTempDirectory(s"tmp_dir_test_${Random.alphanumeric.take(15).mkString}")
      .toFile
    dir.deleteOnExit()
    dir
  }

}
