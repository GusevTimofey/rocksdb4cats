package com.github.timofeyGusev.core

import cats.Monad
import cats.data.NonEmptyList
import cats.effect.{Resource, Sync}
import cats.instances.vector._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import cats.syntax.traverse._
import com.github.timofeyGusev.core.env.HasTraceId
import com.github.timofeyGusev.core.errors.{CoreError, RDBOpErr}
import com.github.timofeyGusev.syntax.resource._
import derevo.derive
import org.rocksdb.{Options, RocksDB => RDB}
import tofu.higherKind.Mid
import tofu.higherKind.derived.representableK
import tofu.logging.{Logging, Logs}
import tofu.syntax.handle._
import tofu.syntax.logging._
import tofu.{Catches, Raise, Throws}

@derive(representableK)
sealed abstract class RocksDB[F[_]] {
  def insert(key: DBKey, value: DBValue): F[Unit]
  def insertMany(toInsert: Vector[(DBKey, DBValue)]): F[Unit]
  def get(key: DBKey): F[Option[DBValue]]
  def getMany(keys: Vector[DBKey]): F[Vector[(DBKey, DBValue)]]
  def remove(key: DBKey): F[Unit]
  def removeMany(keys: Vector[DBKey]): F[Unit]
}

object RocksDB {

  def mk[F[_]: Throws: Catches: HasTraceId: Sync: Raise[*[_], CoreError], I[_]](
    options: Options,
    settings: RocksDBSettings
  )(implicit I: Sync[I], logs: Logs[I, F]): Resource[I, RocksDB[F]] =
    logs.forService[RocksDB[F]].asResource >>= { implicit logging =>
      Resource
        .fromAutoCloseable(I.delay(RDB.open(options, settings.path)))
        .map(
          NonEmptyList
            .of(new RDBMidLogging[F], new RDBMidErrorsHandling[F])
            .reduce attach new Impl[F](_)
        )
    }

  final class Impl[F[_]: Logging](val db: RDB)(implicit
    val F: Sync[F],
    R: Throws[F]
  ) extends RocksDB[F]
    with DBOps[F] {

    override def insert(key: DBKey, value: DBValue): F[Unit] =
      wOp(batch => F.delay(batch.put(key.value, value.value)).as(batch))

    override def insertMany(toInsert: Vector[(DBKey, DBValue)]): F[Unit] =
      wOp(batch =>
        F.delay(toInsert.foreach {
            case (key, value) => batch.put(key.value, value.value)
          })
          .as(batch)
      )

    override def get(key: DBKey): F[Option[DBValue]] =
      rOp[Option[DBValue]] { rOps =>
        F.delay(db.get(rOps, key.value))
          .map(bytes => if (bytes == null) none[DBValue] else DBValue(bytes).some)
      }

    override def getMany(keys: Vector[DBKey]): F[Vector[(DBKey, DBValue)]] =
      rOp[Vector[(DBKey, DBValue)]] { rOps =>
        keys
          .traverse(key => F.delay(key -> db.get(rOps, key.value)))
          .map(_.collect { case (k, v) if v != null => k -> DBValue(v) })
      }

    override def remove(key: DBKey): F[Unit] =
      wOp(batch => F.delay(batch.delete(key.value)).as(batch))

    override def removeMany(keys: Vector[DBKey]): F[Unit] =
      wOp(batch => keys.traverse(key => F.delay(batch.delete(key.value))).as(batch))
  }

  final private class RDBMidLogging[F[_]: HasTraceId: Monad: Logging]
    extends RocksDB[Mid[F, *]] {

    def insert(key: DBKey, value: DBValue): Mid[F, Unit] =
      info"Insert op with key: ${key.asString}" *> _

    def insertMany(toInsert: Vector[(DBKey, DBValue)]): Mid[F, Unit] =
      info"Insert many op with keys: ${toInsert.map(_._1.asString)}" *> _

    def get(key: DBKey): Mid[F, Option[DBValue]] =
      info"Get op with key: ${key.asString}" *> _

    def getMany(keys: Vector[DBKey]): Mid[F, Vector[(DBKey, DBValue)]] =
      info"Get many op with keys: ${keys.map(_.asString)}" *> _

    def remove(key: DBKey): Mid[F, Unit] =
      info"Remove op with key: ${key.asString}" *> _

    def removeMany(keys: Vector[DBKey]): Mid[F, Unit] =
      info"Remove many op with keys: ${keys.map(_.asString)}" *> _
  }

  final private class RDBMidErrorsHandling[F[_]: Monad: Catches: Logging: HasTraceId](
    implicit R: Raise[F, CoreError]
  ) extends RocksDB[Mid[F, *]] {

    override def insert(key: DBKey, value: DBValue): Mid[F, Unit] =
      _.handleWith[Throwable](infoCause"Insert op failed" (_) *> R.raise(RDBOpErr))

    override def insertMany(toInsert: Vector[(DBKey, DBValue)]): Mid[F, Unit] =
      _.handleWith[Throwable](infoCause"Insert many op failed" (_) *> R.raise(RDBOpErr))

    override def get(key: DBKey): Mid[F, Option[DBValue]] =
      _.handleWith[Throwable](infoCause"Get op failed" (_) *> R.raise(RDBOpErr))

    override def getMany(keys: Vector[DBKey]): Mid[F, Vector[(DBKey, DBValue)]] =
      _.handleWith[Throwable](infoCause"Get many op failed" (_) *> R.raise(RDBOpErr))

    override def remove(key: DBKey): Mid[F, Unit] =
      _.handleWith[Throwable](infoCause"Remove op failed" (_) *> R.raise(RDBOpErr))

    override def removeMany(keys: Vector[DBKey]): Mid[F, Unit] =
      _.handleWith[Throwable](infoCause"Remove many op failed" (_) *> R.raise(RDBOpErr))
  }
}
