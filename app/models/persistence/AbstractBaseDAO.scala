package models.persistence

import scala.concurrent.{ExecutionContext, Future}

trait AbstractBaseDAO[E, ID] {

  implicit val ec: ExecutionContext

  def insert(row : E): Future[Int] = insert(Seq(row))
  def insert(rows : Seq[E]): Future[Int]

  def save(row : E): Future[Int] = save(Seq(row))
  def save(rows : Seq[E]): Future[Int]

  //def save(row: A): Future[Int]
  //def getOrCreate(row: A): Future[A]

  def findById(id : ID): Future[Option[E]] = findById(Seq(id)).map(_.headOption)
  def findById(id : Seq[ID]): Future[Seq[E]]

  def findAll: Future[Seq[E]]

  def delete(id : ID): Future[Int] = delete(Seq(id))
  def delete(ids : Seq[ID]): Future[Int]

}
