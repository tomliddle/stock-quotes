package models.persistence

import scala.concurrent.{ExecutionContext, Future}

trait AbstractBaseDAO[E, ID] {

  implicit val ec: ExecutionContext

  def insert(row : E): Future[ID] = insert(Seq(row)).map(_.head)
  def insert(rows : Seq[E]): Future[Seq[ID]]

  def update(row : E): Future[Int]
  def update(rows : Seq[E]): Future[Int] = Future.sequence(rows.map(update)).map(_.sum)

  //def save(row: A): Future[Int]
  //def getOrCreate(row: A): Future[A]

  def findById(id : ID): Future[Option[E]] = findById(Seq(id)).map(_.headOption)
  def findById(id : Seq[ID]): Future[Seq[E]]

  def findAll: Future[Seq[E]]

  def delete(id : ID): Future[Int] = delete(Seq(id))
  def delete(ids : Seq[ID]): Future[Int]

}
