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

package services.mocks

import models.AgentSubscriptionModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import repositories.mocks.MockAgentSubscriptionHoldingPen
import services.ClientSubscriptionDataService
import uk.gov.hmrc.play.test.UnitSpec
import utils.TestConstants._

import scala.concurrent.Future


trait MockClientSubscriptionDataService extends UnitSpec with MockitoSugar with BeforeAndAfterEach {
  val mockClientSubscriptionDataService = mock[ClientSubscriptionDataService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockClientSubscriptionDataService)
  }

  private def mockStore(agentSubscriptionModel: AgentSubscriptionModel)(result: Future[AgentSubscriptionModel]): Unit =
    when(mockClientSubscriptionDataService.store(ArgumentMatchers.eq(agentSubscriptionModel))).thenReturn(result)

  def mockStoreSuccess(agentSubscriptionModel: AgentSubscriptionModel): Unit =
    mockStore(agentSubscriptionModel)(Future.successful(agentSubscriptionModel))

  def mockStoreFailed(agentSubscriptionModel: AgentSubscriptionModel): Unit =
    mockStore(agentSubscriptionModel)(Future.failed(testException))

}

trait TestClientSubscriptionDataService extends MockAgentSubscriptionHoldingPen {

  object TestClientSubscriptionDataService extends ClientSubscriptionDataService(mockAgentSubscriptionHoldingPen)

}