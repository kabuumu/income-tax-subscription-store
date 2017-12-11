package helpers

import models.{AgentSubscriptionModel, Both, DateModel, Property}
import uk.gov.hmrc.domain.Generator


object IntegrationTestConstants {
  lazy val testNino = new Generator().nextNino.nino
  // for the purpose of unit tests we only need a random string for the ARN
  lazy val testArn = new Generator().nextNino.nino

  val testStartDate = DateModel("06", "04", "2017")
  val testEndDate = DateModel("05", "04", "2018")
  val testTradingName = "My trade"
  val testCashOrAccurals = "cash"

  lazy val testAgentPropertySubscription = AgentSubscriptionModel(
    nino = testNino,
    arn = testArn,
    incomeSource = Property,
    otherIncome = false
  )

  lazy val testAgentBothSubscription = AgentSubscriptionModel(
    nino = testNino,
    arn = testArn,
    incomeSource = Both,
    otherIncome = false,
    currentPeriodIsPrior = Some(true),
    accountingPeriodStart = Some(testStartDate),
    accountingPeriodEnd = Some(testEndDate),
    tradingName = Some(testTradingName),
    cashOrAccruals = Some(testCashOrAccurals)
  )
}
