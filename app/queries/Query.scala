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

package queries

import models.UserAnswers
import play.api.libs.json.{JsPath, Reads, Writes}

import scala.util.{Success, Try}

sealed trait Query {

  def path: JsPath
}

trait Gettable[A] extends Query { self =>
  def get(userAnswers: UserAnswers)(implicit ev: Reads[A]): Option[A] =
    userAnswers.get(self)
}

trait Settable[A] extends Query { self =>

  def set(userAnswers: UserAnswers, value: A)(implicit ev: Writes[A]): Try[UserAnswers] =
    userAnswers.set(self, value)

  def cleanup(value: Option[A], userAnswers: UserAnswers): Try[UserAnswers] =
    Success(userAnswers)
}
