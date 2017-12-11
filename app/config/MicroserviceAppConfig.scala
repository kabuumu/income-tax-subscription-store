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

package config

import javax.inject.{Inject, Singleton}

import play.api.{Application, Configuration}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val authURL: String
  val agentHoldingPenExpiryDays: Int
}

@Singleton
class MicroserviceAppConfig @Inject()(val app: Application) extends AppConfig with ServicesConfig {
  val configuration = app.configuration
  override val mode = app.mode

  override lazy val authURL = baseUrl("auth")

  override val agentHoldingPenExpiryDays: Int = {
    val key = "agent-holding-pen.expiry-days"
    configuration.getInt(key)
      .getOrElse(throw new Exception(s"Missing configuration key: $key"))
  }

  override protected def runModeConfiguration: Configuration = configuration
}