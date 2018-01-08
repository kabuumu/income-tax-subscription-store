/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.http.Status._
import play.modules.reactivemongo.ReactiveMongoComponent
import repositories.AgentSubscriptionHoldingPen

import scala.concurrent.ExecutionContext

class ClientSubscriptionDataControllerISpec extends ComponentSpecBase {
  implicit lazy val mongo = app.injector.instanceOf[ReactiveMongoComponent]
  implicit lazy val ec = app.injector.instanceOf[ExecutionContext]
  lazy val appConfig = app.injector.instanceOf[AppConfig]

  object TestAgentSubscriptionHoldingPen extends AgentSubscriptionHoldingPen(appConfig)

  override def beforeEach(): Unit = {
    await(TestAgentSubscriptionHoldingPen.drop)
  }

  "calling POST /client-subscription-data" should {
    "return Created when the nino does not already exist" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      When("I call POST /client-subscription-data")
      val res = IncomeTaxSubscriptionStore.storeSubscription(testNino)(testAgentBothSubscription)

      Then("The result should have a HTTP status of CREATED")
      res should have(
        httpStatus(CREATED)
      )
    }

    "return Internal server error when the nino already exist" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      val insert = TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
      await(insert)

      When("I call POST /client-subscription-data")
      val res = IncomeTaxSubscriptionStore.storeSubscription(testNino)(testAgentBothSubscription)

      Then("The result should have a HTTP status of INTERNAL_SERVER_ERROR")
      res should have(
        httpStatus(INTERNAL_SERVER_ERROR)
      )
    }
  }


  "calling GET /client-subscription-data/:nino" should {
    "return OK when the nino matches already stored subscription data" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      val insert = TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
      await(insert)

      When(s"I call GET /client-subscription-data/$testNino")
      val res = IncomeTaxSubscriptionStore.retrieveSubscription(testNino)

      Then("The result should have a HTTP status of OK")
      res should have(
        httpStatus(OK)
      )
    }

    "return NotFound when the subscription data for that nino does not match" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      val insert = TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
      await(insert)

      val nonMatchingNino = "AA000000A"
      When(s"I call GET /client-subscription-data/$nonMatchingNino")
      val res = IncomeTaxSubscriptionStore.retrieveSubscription(nonMatchingNino)

      Then("The result should have a HTTP status of NotFound")
      res should have(
        httpStatus(NOT_FOUND)
      )
    }
  }

  "calling DELETE /client-subscription-data/:nino" should {
    "return NO_CONTENT if the call is successful" in {
      Given("I setup the wiremock stubs")
      AuthStub.stubAuthSuccess()

      val insert = TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
      await(insert)

      When(s"I call DELETE /client-subscription-data/$testNino")
      val res = IncomeTaxSubscriptionStore.deleteSubscription(testNino)

      Then("The result should have a HTTP status of NO_CONTENT")
      res should have(
        httpStatus(NO_CONTENT)
      )
    }
  }

}

