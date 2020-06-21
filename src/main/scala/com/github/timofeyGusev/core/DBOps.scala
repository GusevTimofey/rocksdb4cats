package com.github.timofeyGusev.core

import cats.effect.{Resource, Sync}
import cats.syntax.flatMap._
import com.github.timofeyGusev.syntax.resource._
import org.rocksdb.{ReadOptions, WriteBatch, WriteOptions, RocksDB => RDB}

trait DBOps[F[_]] {

  val db: RDB

  implicit val F: Sync[F]

  private def rOpResource: Resource[F, ReadOptions] =
    F.delay(new ReadOptions().setSnapshot(db.getSnapshot)).asAutoClosable

  private def wOpResource: Resource[F, WriteBatch] =
    F.delay(new WriteBatch()).asAutoClosable

  def rOp[A](f: ReadOptions => F[A]): F[A] =
    rOpResource.use(f)

  def wOp(f: WriteBatch => F[WriteBatch]): F[Unit] =
    wOpResource.use(batch =>
      f(batch) >>= { batch => F.delay(db.write(new WriteOptions(), batch)) }
    )

  def rwOp[A](f: ReadOptions => F[A], g: WriteBatch => F[WriteBatch]): F[A] =
    rOp(f) flatTap (_ => wOp(g))
}
