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

package helpers

import helpers.servicemocks.{AuditStub, WireMockMethods}
import models.AgentSubscriptionModel
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.WSResponse
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

trait ComponentSpecBase extends UnitSpec
  with GivenWhenThen with TestSuite
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience with Matchers with Assertions
  with WiremockHelper with BeforeAndAfterEach with BeforeAndAfterAll with Eventually
  with CustomMatchers with WireMockMethods {

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWiremock()
    AuditStub.stubAuditing()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  implicit val hc = HeaderCarrier()

  object IncomeTaxSubscriptionStore {
    def get(uri: String): WSResponse = await(buildClient(uri).get())

    def post[T](uri: String, body: T)(implicit writes: Writes[T]): WSResponse = {
      await(
        buildClient(uri)
          .withHeaders(
            "Content-Type" -> "application/json"
          )
          .post(writes.writes(body).toString())
      )
    }

    def store(body: AgentSubscriptionModel): WSResponse = post(s"/client-subscription-data", body)

  }


}
