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

import models.UserAnswers
import models.requests.DataRequest
import play.api.mvc.Request

import scala.concurrent.Future

case class Populate[A](run: DataRequest[_] => Future[A])

object Populate {

  def const[A](run: => A): Populate[A] = Populate(_ => Future.successful(run))
  def fromRequest[A](run: DataRequest[_] => A): Populate[A] = Populate(r => Future.successful(run(r)))
  def fromUserAnswers[A](run: UserAnswers => A): Populate[A] = Populate(r => Future.successful(run(r.userAnswers)))
  def fromFuture[A](run: => Future[A]): Populate[A] = Populate(_ => run)
}
