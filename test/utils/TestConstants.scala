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

package utils

import models._
import uk.gov.hmrc.domain.Generator


object TestConstants {

  lazy val testNino = new Generator().nextNino.nino
  // for the purpose of unit tests we only need a random string for the ARN
  lazy val testArn = new Generator().nextNino.nino

  val testStartDate = DateModel("06", "04", "2017")
  val testEndDate = DateModel("05", "04", "2018")
  val testTradingName = "My trade"
  val testCashOrAccurals = "cash"

  lazy val testAgentPropertySubscription = AgentSubscriptionModel(
    arn = testArn,
    incomeSource = Property,
    otherIncome = false
  )

  lazy val testAgentPropertyPersist = AgentSubscriptionPersistModel(
    testNino,
    testAgentPropertySubscription
  )

  lazy val testAgentBothSubscription = AgentSubscriptionModel(
    arn = testArn,
    incomeSource = Both,
    otherIncome = false,
    currentPeriodIsPrior = Some(true),
    accountingPeriodStart = Some(testStartDate),
    accountingPeriodEnd = Some(testEndDate),
    tradingName = Some(testTradingName),
    cashOrAccruals = Some(testCashOrAccurals)
  )

  lazy val testAgentBothPersist = AgentSubscriptionPersistModel(
    testNino,
    testAgentBothSubscription
  )


  val testException = new Exception("an error")

}
