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

package services

import reactivemongo.api.commands.DefaultWriteResult
import services.mocks.TestClientSubscriptionDataService
import uk.gov.hmrc.play.test.UnitSpec
import utils.TestConstants.{testAgentBothSubscription, _}

import scala.concurrent.ExecutionContext.Implicits.global

class ClientSubscriptionDataServiceSpec extends UnitSpec with TestClientSubscriptionDataService {

  "store" should {
    "return the model when it is successfully stored" in {
      mockStore(testAgentBothPersist)

      val res = TestClientSubscriptionDataService.store(testNino, testAgentBothSubscription)
      await(res) shouldBe testAgentBothSubscription
    }
    "return the failure when the storage fails" in {
      mockStoreFailed(testAgentBothPersist)

      val res = TestClientSubscriptionDataService.store(testNino, testAgentBothSubscription)
      intercept[Exception](await(res)) shouldBe testException
    }
  }

  "retrieveSubscriptionData" should {
    "return the model when the subscription data is successfully retrieved" in {
      mockRetrieve(testNino)

      val res = TestClientSubscriptionDataService.retrieveSubscriptionData(testNino)
      await(res) shouldBe Some(testAgentBothSubscription)
    }
    "return the nothing when the subscription data cannot be found" in {
      mockRetrieveNotFound(testNino)

      val res = TestClientSubscriptionDataService.retrieveSubscriptionData(testNino)
      await(res) shouldBe None
    }
    "return the failure when the storage fails" in {
      mockRetrieveFailed(testNino)

      val res = TestClientSubscriptionDataService.retrieveSubscriptionData(testNino)
      intercept[Exception](await(res)) shouldBe testException
    }
  }

  "delete" should {
    "return the the write result from calling the repo " in {
      val testWriteResult = DefaultWriteResult(ok = true, 1, Nil, None, None, None)
      mockDelete(testNino)(testWriteResult)

      val res = TestClientSubscriptionDataService.delete(testNino)
      await(res) shouldBe testWriteResult
    }

    "return the failure when the storage fails" in {
      mockDeleteFailed(testNino)

      val res = TestClientSubscriptionDataService.delete(testNino)
      intercept[Exception](await(res)) shouldBe testException
    }
  }
}
