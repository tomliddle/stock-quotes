package controllers

import javax.inject._

import models.entities.Stock
import models.persistence.AbstractBaseDAO
import models.persistence.SlickTables.StocksTable
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(stockDAO : AbstractBaseDAO[StocksTable,Stock])(implicit exec: ExecutionContext) extends Controller {

  def stock(id : String): Action[AnyContent] = Action.async {
    stockDAO.findById(id).map(x => x.fold(NoContent)(res => Ok(Json.toJson(res))))
  }

  def insertStock(id: String): Action[JsValue] = Action.async(parse.json) {
    request =>
      {
        for {
          name <- (request.body \ "name").asOpt[String]
          desc <- (request.body \ "desc").asOpt[String]
        } yield {
          stockDAO.insert(Stock("0", name, desc)).map { n => Created("Id of Stock Added : " + n) }.recoverWith {
            case e => Future {
              InternalServerError("There was an error at the server")
            }
          }
        }
      }.getOrElse(Future{BadRequest("Wrong json format")})
  }

  def deleteStock(id: String): Action[JsValue] = Action.async(parse.json) {
    request => {
      stockDAO.deleteById(id).map {
        case 1 => Ok("")
        case 0 => NotFound("")
        case _ => Ok("more than 1 found")
      }
    }
  }
}
