package controllers

import dao.ClubDao
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Play controller for providing two API endpoints, one for submitting a new club and
 * it's members and one for listing the clubs and all their members.
 *
 * @param clubDao for accessing the database
 */
@Singleton
class ClubController @Inject() (
  clubDao: ClubDao,
  cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val memberFormat = Json.format[Member]
  implicit val clubFormat = Json.format[Club]

  def storeClub: Action[JsValue] = Action.async(parse.json) { request =>
    val placeResult = request.body.validate[Club]
    placeResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      club => {
        clubDao.insert(club).map(_ => Ok)
      }
    )
  }

  def getClubs: Action[AnyContent] = Action.async {
    clubDao.all().map(clubs => Ok(Json.toJson(clubs)))
  }
}