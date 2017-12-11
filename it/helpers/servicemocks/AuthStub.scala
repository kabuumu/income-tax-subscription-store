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

package helpers.servicemocks

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.auth.UserIds
import play.api.http.HeaderNames
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}

object AuthStub extends WireMockMethods {
  val authIDs = "/uri/to/ids"
  val authority = "/auth/authorise"

  val gatewayID = "12345"
  val internalID = "internal"
  val externalID = "external"
  val userIDs = UserIds(internalId = internalID, externalId = externalID)

  def stubAuthSuccess(): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = OK, body = successfulAuthResponse)
  }

  private def exceptionHeaders(value: String) = Map(HeaderNames.WWW_AUTHENTICATE -> s"""MDTP detail="$value"""")

  def stubAuthFailure(): StubMapping = {
    when(method = POST, uri = authority)
      .thenReturn(status = UNAUTHORIZED, headers = exceptionHeaders("MissingBearerToken"))
  }

  val successfulAuthResponse: JsObject = {
    Json.obj(
      "internalId" -> userIDs.internalId
    )
  }

}
