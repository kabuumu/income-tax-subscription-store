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

import models.{AgentSubscriptionModel, DateModel, IncomeSourceType}
import play.api.libs.json.{Json, _}

object AgentSubscriptionModelConstants {
  val ninoKey = "_id"
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

object AgentSubscriptionModelReads extends Reads[AgentSubscriptionModel] {

  import AgentSubscriptionModelConstants._

  override def reads(json: JsValue): JsResult[AgentSubscriptionModel] = for {
    nino <- (json \ ninoKey).validate[String]
    arn <- (json \ arnKey).validate[String]
    incomeSource <- (json \ incomeSourceKey).validate[IncomeSourceType]
    otherIncome <- (json \ otherIncomeKey).validate[Boolean]
    currentPeriodIsPrior <- (json \ currentPeriodIsPriorKey).validateOpt[Boolean]
    accountingPeriodStart <- (json \ accountingPeriodStartKey).validateOpt[DateModel]
    accountingPeriodEnd <- (json \ accountingPeriodEndKey).validateOpt[DateModel]
    tradingName <- (json \ tradingNameKey).validateOpt[String]
    cashOrAccruals <- (json \ cashOrAccrualsKey).validateOpt[String]
  } yield AgentSubscriptionModel(
    nino = nino,
    arn = arn,
    incomeSource = incomeSource,
    otherIncome = otherIncome,
    currentPeriodIsPrior = currentPeriodIsPrior,
    accountingPeriodStart = accountingPeriodStart,
    accountingPeriodEnd = accountingPeriodEnd,
    tradingName = tradingName,
    cashOrAccruals = cashOrAccruals
  )
}


object AgentSubscriptionModelWrites extends OWrites[AgentSubscriptionModel] {

  import AgentSubscriptionModelConstants._

  override def writes(model: AgentSubscriptionModel): JsObject = Json.obj(
    ninoKey -> model.nino,
    arnKey -> model.arn,
    incomeSourceKey -> model.incomeSource,
    otherIncomeKey -> model.otherIncome,
    currentPeriodIsPriorKey -> model.currentPeriodIsPrior,
    accountingPeriodStartKey -> model.accountingPeriodStart,
    accountingPeriodEndKey -> model.accountingPeriodEnd,
    tradingNameKey -> model.tradingName,
    cashOrAccrualsKey -> model.cashOrAccruals,
    timestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
  )
}


