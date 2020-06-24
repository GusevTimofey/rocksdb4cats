package com.github.gusev.syntax

import cats.Applicative
import cats.effect.{Resource, Sync}
import com.github.gusev.syntax.ResourceSyntax.{AutoClosableResourceOps, ResourceOps}

trait ResourceSyntax {

  implicit final def syntaxResource[F[_], A](fa: F[A]): ResourceOps[F, A] =
    new ResourceOps(fa)

  implicit final def autoClosableSyntaxResource[F[_], A <: AutoCloseable](
    fa: F[A]
  ): AutoClosableResourceOps[F, A] =
    new AutoClosableResourceOps(fa)
}

object ResourceSyntax {

  final private[syntax] class ResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def asResource(implicit F: Applicative[F]): Resource[F, A] = Resource.liftF(fa)
  }

  final private[syntax] class AutoClosableResourceOps[F[_], A <: AutoCloseable](
    private val fa: F[A]
  ) extends AnyVal {

    def asAutoClosable(implicit F: Sync[F]): Resource[F, A] =
      Resource.fromAutoCloseable(fa)
  }
}
