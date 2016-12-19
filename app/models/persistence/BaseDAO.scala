package models.persistence

import models.entities.BaseEntity
import models.persistence.SlickTables.BaseTable
import play.api.Application
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.{CanBeQueryCondition, TableQuery}
import scala.concurrent.Future


trait AbstractBaseDAO[T,A] {
  def insert(row : A): Future[String]
  def insert(rows : Seq[A]): Future[Seq[String]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : String): Future[Option[A]]
  def findAll: Future[Seq[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id : String): Future[Int]
  def deleteById(ids : Seq[String]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
}


class BaseDAO[T <: BaseTable[A], A <: BaseEntity]()(implicit val tableQ: TableQuery[T], implicit val app: Application) extends AbstractBaseDAO[T,A] with HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._

  def insert(row : A): Future[String] ={
    insert(Seq(row)).map(_.head)
  }

  def insert(rows : Seq[A]): Future[Seq[String]] ={
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row : A): Future[Int] = {
    if (row.isValid) db.run(tableQ.filter(_.id === row.id).update(row))
    else Future{0}
  }

  def update(rows : Seq[A]): Future[Unit] = {
    db.run(DBIO.seq(rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r)): _*))
  }

  def findById(id : String): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def findAll: Future[Seq[A]] = {
    db.run(tableQ.map(identity).result)
  }

  def deleteById(id : String): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids : Seq[String]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

}