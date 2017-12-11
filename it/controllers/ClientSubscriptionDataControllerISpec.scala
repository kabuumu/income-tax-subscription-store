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

import config.AppConfig
import helpers.ComponentSpecBase
import helpers.IntegrationTestConstants._
import helpers.servicemocks._
import org.scalatest.BeforeAndAfterEach
import play.api.http.Status._
import play.modules.reactivemongo.ReactiveMongoComponent
import repositories.AgentSubscriptionHoldingPen

import scala.concurrent.ExecutionContext

class ClientSubscriptionDataControllerISpec extends ComponentSpecBase with BeforeAndAfterEach {
  implicit lazy val mongo = app.injector.instanceOf[ReactiveMongoComponent]
  implicit lazy val ec = app.injector.instanceOf[ExecutionContext]
  lazy val appConfig = app.injector.instanceOf[AppConfig]

  object TestAgentSubscriptionHoldingPen extends AgentSubscriptionHoldingPen(appConfig)

  override def beforeEach(): Unit = {
    await(TestAgentSubscriptionHoldingPen.drop)
  }

  "calling /client-subscription-data" should {
    "return Created when the nino does not already exist" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      When("I call POST /client-subscription-data")
      val res = IncomeTaxSubscriptionStore.store(testAgentBothSubscription)

      Then("The result should have a HTTP status of CREATED")
      res should have(
        httpStatus(CREATED)
      )
    }

    "return Internal server error when the nino already exist" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      val insert = TestAgentSubscriptionHoldingPen.store(testAgentPropertySubscription)
      await(insert)

      When("I call POST /client-subscription-data")
      val res = IncomeTaxSubscriptionStore.store(testAgentBothSubscription)

      Then("The result should have a HTTP status of INTERNAL_SERVER_ERROR")
      res should have(
        httpStatus(INTERNAL_SERVER_ERROR)
      )
    }
  }

}

