/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.questionPageControllers

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsPath, JsString, Json}
import play.api.test.FakeRequest
import queries.Gettable

import scala.concurrent.Future

class PopulateSpec extends SpecBase with ScalaFutures {

  val dataRequest =
    DataRequest(FakeRequest(), "userId", UserAnswers("userAnswers", Json.obj("key1" -> JsString("value1"))))

  "const should" - {
    "return input value" in {
      Populate.const("result").run(dataRequest).futureValue mustBe "result"
    }
  }

  "fromRequest should" - {
    "return value based on request" in {
      Populate.fromRequest(_.userId).run(dataRequest).futureValue mustBe "userId"
    }
  }

  "fromUserAnswers should" - {
    "return value when exists in user answers" in {
      val page = new Gettable[String] {
        override def path: JsPath = JsPath \ "key1"
      }

      Populate.fromUserAnswers(_.get(page)).run(dataRequest).futureValue mustBe Some("value1")
    }

    "return None when does not exist in user answers" in {
      val page = new Gettable[String] {
        override def path: JsPath = JsPath \ "key2"
      }

      Populate.fromUserAnswers(_.get(page)).run(dataRequest).futureValue mustBe None
    }
  }

  "fromFuture should" - {
    "return value from future" in {
      val future = Future.successful(Future.successful("result")).flatten

      Populate.fromFuture(future).run(dataRequest).futureValue mustBe "result"
    }
  }
}