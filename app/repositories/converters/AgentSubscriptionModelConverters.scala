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

package repositories.converters

import java.time.Instant

import models.{AgentSubscriptionModel, AgentSubscriptionPersistModel, DateModel, IncomeSourceType}
import play.api.libs.json.{Json, _}

object AgentSubscriptionPersistModelConstants {
  val ninoKey = "_id"
  val subscriptionKey = "subscription"
  val arnKey = "arn"
  val incomeSourceKey = "incomeSource"
  val timestampKey = "creationTimestamp"
  val otherIncomeKey = "otherIncome"
  val currentPeriodIsPriorKey = "currentPeriodIsPrior"
  val accountingPeriodStartKey = "accountingPeriodStart"
  val accountingPeriodEndKey = "accountingPeriodEnd"
  val tradingNameKey = "tradingName"
  val cashOrAccrualsKey = "cashOrAccruals"
}

object AgentSubscriptionPersistModelReads extends Reads[AgentSubscriptionPersistModel] {

  import AgentSubscriptionPersistModelConstants._

  override def reads(json: JsValue): JsResult[AgentSubscriptionPersistModel] = for {
    nino <- (json \ ninoKey).validate[String]
    sub = json \ subscriptionKey
    arn <- (sub \ arnKey).validate[String]
    incomeSource <- (sub \ incomeSourceKey).validate[IncomeSourceType]
    otherIncome <- (sub \ otherIncomeKey).validate[Boolean]
    currentPeriodIsPrior <- (sub \ currentPeriodIsPriorKey).validateOpt[Boolean]
    accountingPeriodStart <- (sub \ accountingPeriodStartKey).validateOpt[DateModel]
    accountingPeriodEnd <- (sub \ accountingPeriodEndKey).validateOpt[DateModel]
    tradingName <- (sub \ tradingNameKey).validateOpt[String]
    cashOrAccruals <- (sub \ cashOrAccrualsKey).validateOpt[String]
  } yield AgentSubscriptionPersistModel(nino = nino,
    AgentSubscriptionModel(
      arn = arn,
      incomeSource = incomeSource,
      otherIncome = otherIncome,
      currentPeriodIsPrior = currentPeriodIsPrior,
      accountingPeriodStart = accountingPeriodStart,
      accountingPeriodEnd = accountingPeriodEnd,
      tradingName = tradingName,
      cashOrAccruals = cashOrAccruals
    )
  )
}


object AgentSubscriptionPersistModelWrites extends OWrites[AgentSubscriptionPersistModel] {

  import AgentSubscriptionPersistModelConstants._

  override def writes(model: AgentSubscriptionPersistModel): JsObject = {
    val sub = model.subscription
    Json.obj(
      ninoKey -> model.nino,
      subscriptionKey -> Json.obj(
        arnKey -> sub.arn,
        incomeSourceKey -> sub.incomeSource,
        otherIncomeKey -> sub.otherIncome,
        currentPeriodIsPriorKey -> sub.currentPeriodIsPrior,
        accountingPeriodStartKey -> sub.accountingPeriodStart,
        accountingPeriodEndKey -> sub.accountingPeriodEnd,
        tradingNameKey -> sub.tradingName,
        cashOrAccrualsKey -> sub.cashOrAccruals
      ),
      timestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
    )
  }
}


