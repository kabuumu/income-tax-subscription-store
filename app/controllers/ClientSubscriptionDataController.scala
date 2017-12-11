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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.{AuthService, ClientSubscriptionDataService}
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext



@Singleton
class ClientSubscriptionDataController @Inject()(authService: AuthService,
                                                 clientSubscriptionService: ClientSubscriptionDataService,
                                                 implicit val ec: ExecutionContext) extends  BaseController{

  import authService._

  def retrieveSubscriptionData(nino: String): Action[AnyContent] = {
      Action.async { implicit request =>
        authorised() {
          clientSubscriptionService.retrieveSubscriptionData(nino) map {
            case Some(subscriptionData) => Ok(Json.toJson(subscriptionData))
            case None => BadRequest
          }
        }
      }
  }
}

