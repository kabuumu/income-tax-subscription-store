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

package services.mocks

import models.AgentSubscriptionModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import reactivemongo.api.commands.{DefaultWriteResult, WriteResult}
import repositories.mocks.MockAgentSubscriptionHoldingPen
import services.ClientSubscriptionDataService
import uk.gov.hmrc.play.test.UnitSpec
import utils.TestConstants._

import scala.concurrent.{ExecutionContext, Future}


trait MockClientSubscriptionDataService extends UnitSpec with MockitoSugar with BeforeAndAfterEach {
  val mockClientSubscriptionDataService = mock[ClientSubscriptionDataService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockClientSubscriptionDataService)
  }

  private def mockStore(nino: String, agentSubscriptionModel: AgentSubscriptionModel)(result: Future[AgentSubscriptionModel]): Unit =
    when(mockClientSubscriptionDataService.store(ArgumentMatchers.eq(nino), ArgumentMatchers.eq(agentSubscriptionModel))(ArgumentMatchers.any[ExecutionContext]))
      .thenReturn(result)

  private def mockRetrieve(nino: String)(result: Future[Option[AgentSubscriptionModel]]): Unit =
    when(mockClientSubscriptionDataService.retrieveSubscriptionData(ArgumentMatchers.eq(nino))(ArgumentMatchers.any[ExecutionContext])).thenReturn(result)

  def mockStoreSuccess(nino: String, agentSubscriptionModel: AgentSubscriptionModel): Unit =
    mockStore(nino: String, agentSubscriptionModel)(Future.successful(agentSubscriptionModel))

  def mockStoreFailed(nino: String, agentSubscriptionModel: AgentSubscriptionModel): Unit =
    mockStore(nino: String, agentSubscriptionModel)(Future.failed(testException))

  def mockRetrieveFound(nino: String)(agentSubscriptionModel: AgentSubscriptionModel): Unit =
    mockRetrieve(nino)(Future.successful(Some(agentSubscriptionModel)))

  def mockRetrieveNotFound(nino: String): Unit =
    mockRetrieve(nino)(Future.successful(None))

  def mockRetrieveFailure(nino: String): Unit =
    mockRetrieve(nino)(Future.failed(testException))

  private def mockDelete(nino: String)(result: Future[WriteResult]): Unit =
    when(mockClientSubscriptionDataService.delete(ArgumentMatchers.eq(nino))(ArgumentMatchers.any[ExecutionContext])).thenReturn(result)

  def mockDeleteOk(nino: String): Unit =
    mockDelete(nino)(Future.successful(DefaultWriteResult(ok = true, 1, Nil, None, None, None)))

  def mockDeleteFailure(nino: String): Unit =
    mockDelete(nino)(Future.failed(testException))

}

trait TestClientSubscriptionDataService extends MockAgentSubscriptionHoldingPen {

  object TestClientSubscriptionDataService extends ClientSubscriptionDataService(mockAgentSubscriptionHoldingPen)

}