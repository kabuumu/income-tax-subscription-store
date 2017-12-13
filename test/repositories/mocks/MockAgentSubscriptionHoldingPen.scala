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

package repositories.mocks

import models.AgentSubscriptionPersistModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import repositories.AgentSubscriptionHoldingPen
import utils.TestConstants._

import scala.concurrent.Future

trait MockAgentSubscriptionHoldingPen extends MockitoSugar {
  val mockAgentSubscriptionHoldingPen = mock[AgentSubscriptionHoldingPen]

  def mockStore(subscription: AgentSubscriptionPersistModel): Unit = {
    when(mockAgentSubscriptionHoldingPen.store(ArgumentMatchers.eq(subscription)))
      .thenReturn(Future.successful(subscription))
  }

  def mockStoreFailed(subscription: AgentSubscriptionPersistModel): Unit = {
    when(mockAgentSubscriptionHoldingPen.store(ArgumentMatchers.eq(subscription)))
      .thenReturn(Future.failed(testException))
  }

  def mockRetrieve(nino: String): Unit = {
    when(mockAgentSubscriptionHoldingPen.retrieve(ArgumentMatchers.eq(nino)))
      .thenReturn(Future.successful(Some(testAgentBothPersist)))
  }

  def mockRetrieveNotFound(nino: String): Unit = {
    when(mockAgentSubscriptionHoldingPen.retrieve(ArgumentMatchers.eq(nino)))
      .thenReturn(Future.successful(None))
  }

  def mockRetrieveFailed(nino: String): Unit = {
    when(mockAgentSubscriptionHoldingPen.retrieve(ArgumentMatchers.eq(nino)))
      .thenReturn(Future.failed(testException))
  }
}
