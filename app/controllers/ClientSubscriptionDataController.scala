/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import javax.inject.{Inject, Singleton}

import models.AgentSubscriptionModel
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent}
import services.{AuthService, ClientSubscriptionDataService}
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ClientSubscriptionDataController @Inject()(authService: AuthService,
                                                 clientSubscriptionService: ClientSubscriptionDataService)
                                                (implicit ec: ExecutionContext) extends BaseController {

  import authService._

  def store(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    authorised() {
      val json = request.body
      Json.fromJson[AgentSubscriptionModel](json) match {
        case JsSuccess(submission, _) =>
          clientSubscriptionService.store(submission) map (storedResult => Created(Json.toJson(storedResult)))
        case JsError(failure) =>
          Logger.debug("ClientSubscriptionDataController.store input failed parsing: " + failure.toString())
          Future.successful(BadRequest)
      }
    }
  }


  def retrieveSubscriptionData(nino: String): Action[AnyContent] = {
    Action.async { implicit request =>
      authorised() {
        clientSubscriptionService.retrieveSubscriptionData(nino) map {
          case Some(subscriptionData) => Ok(Json.toJson(subscriptionData))
          case None => NotFound
        }
      }
    }
  }
}

