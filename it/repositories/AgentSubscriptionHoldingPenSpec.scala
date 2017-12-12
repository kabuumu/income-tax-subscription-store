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

package repositories

import helpers.IntegrationTestConstants._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext

class AgentSubscriptionHoldingPenSpec extends UnitSpec with GuiceOneAppPerSuite with BeforeAndAfterEach {
  val TestAgentSubscriptionHoldingPen = app.injector.instanceOf[AgentSubscriptionHoldingPen]
  implicit val ec = app.injector.instanceOf[ExecutionContext]

  override def beforeEach(): Unit = {
    await(TestAgentSubscriptionHoldingPen.drop)
  }

  "store" should {
    "return the model when it is successfully inserted" in {
      List(testAgentPropertyPersist, testAgentBothPersist).foreach {
        testModel =>
          withClue(s"Using model: $testModel") {
            val (insertRes, stored) = await(for {
              insertRes <- TestAgentSubscriptionHoldingPen.store(testModel)
              stored <- TestAgentSubscriptionHoldingPen.find("_id" -> testNino)
            } yield (insertRes, stored))

            stored.size shouldBe 1
            insertRes shouldBe stored.head

            await(TestAgentSubscriptionHoldingPen.drop)
          }
      }

    }

    "fail when a duplicate is created" in {
      val res = for {
        _ <- TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
        _ <- TestAgentSubscriptionHoldingPen.store(testAgentBothPersist)
      } yield ()

      intercept[Exception](await(res))
    }
  }

  "retrieve" should {
    "return None when there is data" in {
      val res = await(TestAgentSubscriptionHoldingPen.retrieve(testNino))
      res shouldBe empty
    }

    "return a AgentSubscriptionModel if there is data" in {
      val res = await(for {
        insertRes <- TestAgentSubscriptionHoldingPen.store(testAgentPropertyPersist)
        stored <- TestAgentSubscriptionHoldingPen.retrieve(testNino)
      } yield stored)

      res shouldBe Some(testAgentPropertyPersist)
    }
  }
}
