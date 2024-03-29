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

package repositories

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.AgentSubscriptionPersistModel
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.IndexType.Ascending
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import repositories.converters.{AgentSubscriptionPersistModelConstants, AgentSubscriptionPersistModelReads, AgentSubscriptionPersistModelWrites}
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentSubscriptionHoldingPen @Inject()(config: AppConfig)(implicit mongo: ReactiveMongoComponent, ec: ExecutionContext)
  extends ReactiveRepository[AgentSubscriptionPersistModel, BSONObjectID](
    "agentSubscriptionHoldingPen",
    mongo.mongoConnector.db,
    OFormat(AgentSubscriptionPersistModelReads, AgentSubscriptionPersistModelWrites),
    ReactiveMongoFormats.objectIdFormats
  ) {

  def store(key: AgentSubscriptionPersistModel): Future[AgentSubscriptionPersistModel] = {
    insert(key) map (_ => key)
  }

  //TODO decide what to do if there's more than one, e.g. from different agents
  def retrieve(nino: String): Future[Option[AgentSubscriptionPersistModel]] = {
    find(AgentSubscriptionPersistModelConstants.ninoKey -> nino) map (_.headOption)
  }

  def delete(nino: String): Future[WriteResult] = {
    remove(AgentSubscriptionPersistModelConstants.ninoKey -> nino)
  }

  private lazy val ttlIndex = Index(
    Seq((AgentSubscriptionPersistModelConstants.timestampKey, IndexType(Ascending.value))),
    name = Some("agentSubscriptionExpires"),
    unique = false,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    options = BSONDocument("expireAfterSeconds" -> TimeUnit.DAYS.toSeconds(config.agentHoldingPenExpiryDays))
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(ttlIndex.name.get) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()
}
