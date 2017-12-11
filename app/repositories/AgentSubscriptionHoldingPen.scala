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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.AgentSubscriptionModel
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import repositories.converters.{AgentSubscriptionModelConstants, AgentSubscriptionModelReads, AgentSubscriptionModelWrites}
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentSubscriptionHoldingPen @Inject()(config: AppConfig)(implicit mongo: ReactiveMongoComponent, ec: ExecutionContext)
  extends ReactiveRepository[AgentSubscriptionModel, BSONObjectID](
    "agentSubscriptionHoldingPen",
    mongo.mongoConnector.db,
    OFormat(AgentSubscriptionModelReads, AgentSubscriptionModelWrites),
    ReactiveMongoFormats.objectIdFormats
  ) {

  def store(key: AgentSubscriptionModel): Future[AgentSubscriptionModel] = {
    insert(key) map (_ => key)
  }

  //TODO decide what to do if there's more than one, e.g. from different agents
  def retrieve(nino: String): Future[Option[AgentSubscriptionModel]] = {
    find(AgentSubscriptionModelConstants.ninoKey -> nino) map (_.headOption)
  }

  private lazy val ttlIndex = Index(
    Seq((AgentSubscriptionModelConstants.timestampKey, IndexType(Ascending.value))),
    name = Some("agentSubscriptionExpires"),
    unique = false,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    options = BSONDocument("expireAfterSeconds" -> config.agentHoldingPenExpiryDays * 24 * 60)
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(ttlIndex.name.get) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()
}
