package models.persistence

import scala.concurrent.{ExecutionContext, Future}

/**
  * Abstract DAO class. Should save a couple of implementations
  * Doesn't allow us to abstract findBy((E) => Boolean) as some macros in Slick don't allow it
  * @tparam E Entity
  * @tparam ID Id type
  */
trait AbstractBaseDAO[E, ID] {

  implicit val ec: ExecutionContext

  def insert(row : E): Future[Int] = insert(Seq(row))
  def insert(rows : Seq[E]): Future[Int]

  def save(row : E): Future[Int] = save(Seq(row))
  def save(rows : Seq[E]): Future[Int]

  def findById(id : ID): Future[Option[E]] = findById(Seq(id)).map(_.headOption)
  def findById(id : Seq[ID]): Future[Seq[E]]

  def findAll: Future[Seq[E]]

  def delete(id : ID): Future[Int] = delete(Seq(id))
  def delete(ids : Seq[ID]): Future[Int]

}
