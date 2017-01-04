package controllers

import javax.inject._
import entities.Stock
import models.persistence.StockPersistence
import models.util.JsonConverters._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
  * Handles the stock CRUD operations
  * @param stockDAO Stock DAO
  */
@Singleton
class StockController @Inject()(stockDAO: StockPersistence)(implicit ec: ExecutionContext) extends Controller {

  private val logger = Logger(getClass)

  def allStock: Action[AnyContent] = Action.async {
    stockDAO.findAll.map(x => Ok(Json.toJson(x)))
  }

  def stock(id : String): Action[AnyContent] = Action.async {
    stockDAO.findById(id).map(x => x.fold(NoContent)(res => Ok(Json.toJson(res))))
  }

  def saveStock(): Action[JsValue] = Action.async(parse.json) {
    request =>
      Json.fromJson[Stock](request.body) match {
        case JsSuccess(s: Stock, path: JsPath) =>
          stockDAO.save(s).map { n => Created("Id of Stock Added : " + n) }.recoverWith {
            case e => Future.successful { InternalServerError(s"There was an error at the server ${e.getMessage}") }
          }

        case e: JsError =>
          logger.warn(s"${e.errors}")
          Future.successful(UnsupportedMediaType)
      }
  }

  def deleteStock(id: String): Action[JsValue] = Action.async(parse.json) {
    request => {
      stockDAO.delete(id).map {
        case 1 => Ok("")
        case 0 => NotFound("")
        case _ => Ok("more than 1 found")
      }
    }
  }
}
