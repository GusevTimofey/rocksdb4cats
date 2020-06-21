package com.github.timofeyGusev.core

import tofu.HasContext

package object env {

  type HasTraceId[F[_]] = F HasContext TraceId

}
