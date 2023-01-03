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

package pages

import models.BankAccount
import play.api.libs.json.JsPath

case class Question3Page(index: Int) extends QuestionPage[BankAccount] {

  override def path: JsPath = Question3Page.parentPath \ index.toString

  override def toString: String = s"question3"
}

object Question3Page {
  val parentPath: JsPath = JsPath \ toString
}