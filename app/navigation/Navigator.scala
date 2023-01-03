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

package navigation

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = page match {
    case Question1Page => routes.Question2Controller.onPageLoad(mode)
    case Question2Page => routes.Question3Controller.onPageLoad(NormalMode, 1)
    case Question3Page(page) => routes.Question3AddAnotherController.onPageLoad(NormalMode, page)
    case Question3AddAnotherPage(page) =>
      if (userAnswers.get(Question3AddAnotherPage(page)).contains(true)) {
        routes.Question3Controller.onPageLoad(NormalMode, page + 1)
      } else {
        routes.Question4aController.onPageLoad(NormalMode)
      }
    case Question4aPage => routes.Question4bController.onPageLoad(NormalMode)
    case Question4bPage => routes.Question4cController.onPageLoad(NormalMode)
    case Question4cPage => routes.Question4dController.onPageLoad(NormalMode)
    case Question4dPage => routes.CheckYourAnswersController.onPageLoad
    case _ => routes.IndexController.onPageLoad
  }
}
