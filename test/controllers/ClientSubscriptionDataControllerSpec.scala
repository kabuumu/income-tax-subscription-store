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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import services.mocks.{MockAuthService, MockClientSubscriptionDataService}
import uk.gov.hmrc.play.test.UnitSpec
import utils.MaterializerSupport
import utils.TestConstants._
import play.api.http.Status._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ClientSubscriptionDataControllerSpec extends UnitSpec with MaterializerSupport with MockClientSubscriptionDataService with MockAuthService {

  object TestClientSubscriptionDataController extends ClientSubscriptionDataController(
    mockAuthService,
    mockClientSubscriptionDataService,
    implicitly[ExecutionContext]
  )

  s"store" should {
    "return CREATED when the agent submission is successfully stored against the nino" in {
      mockAuthSuccess()
      mockStoreSuccess(testAgentBothSubscription)

      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.toJson(testAgentBothSubscription))

      val res: Future[Result] = TestClientSubscriptionDataController.store()(request)

      status(res) shouldBe CREATED
    }

    "fail if the storage fails" in {
      mockAuthSuccess()
      mockStoreFailed(testAgentBothSubscription)

      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.toJson(testAgentBothSubscription))

      val res: Future[Result] = TestClientSubscriptionDataController.store()(request)

      intercept[Exception](await(res)) shouldBe testException
    }

    "return a bad request when the json cannot be parsed" in {
      mockAuthSuccess()
      mockStoreFailed(testAgentBothSubscription)

      val request: FakeRequest[JsValue] = FakeRequest().withBody(Json.obj())

      val res: Future[Result] = TestClientSubscriptionDataController.store()(request)

      status(res) shouldBe BAD_REQUEST
    }
  }


  "retrieveSubscriptionData" should {
    "return 'Ok' if the client subscription data is successfully retrieved" in {
      mockAuthSuccess()
      mockRetrieveFound(testNino)(testAgentBothSubscription)

      val res: Future[Result] = TestClientSubscriptionDataController.retrieveSubscriptionData(testNino)(FakeRequest())

      status(res) shouldBe OK
    }

    "return 'BadRequest' if the client subscription data cannot be found" in {
      mockAuthSuccess()
      mockRetrieveNotFound(testNino)

      val res: Future[Result] = TestClientSubscriptionDataController.retrieveSubscriptionData(testNino)(FakeRequest())

      status(res) shouldBe BAD_REQUEST
    }

    "fail if exception occurs" in {
      mockAuthSuccess()
      mockRetrieveFailure(testNino)

      val res: Future[Result] = TestClientSubscriptionDataController.retrieveSubscriptionData(testNino)(FakeRequest())

      intercept[Exception](await(res)) shouldBe testException

    }
  }

}
