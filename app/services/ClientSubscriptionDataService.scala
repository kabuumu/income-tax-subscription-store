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

import javax.inject.{Inject, Singleton}

import models.{AgentSubscriptionModel, AgentSubscriptionPersistModel}
import reactivemongo.api.commands.WriteResult
import repositories.AgentSubscriptionHoldingPen

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ClientSubscriptionDataService @Inject()(agentSubscriptionHoldingPen: AgentSubscriptionHoldingPen) {

  def store(nino: String, agentSubscriptionModel: AgentSubscriptionModel)(implicit ec: ExecutionContext): Future[AgentSubscriptionModel] =
    agentSubscriptionHoldingPen.store(AgentSubscriptionPersistModel(nino, agentSubscriptionModel)).map(_.subscription)

  def retrieveSubscriptionData(nino: String)(implicit ec: ExecutionContext): Future[Option[AgentSubscriptionModel]] =
    agentSubscriptionHoldingPen.retrieve(nino).map(_.map(_.subscription))

  def delete(nino: String)(implicit ec: ExecutionContext): Future[WriteResult] =
    agentSubscriptionHoldingPen.delete(nino)
}
