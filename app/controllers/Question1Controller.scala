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

package controllers

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.questionPageControllers.{ModeQuestionPage, Populate, YesNoPageController}
import forms.YesNoPageFormProvider
import models.{Mode, UrlParams}
import navigation.Navigator
import pages.{Question1Page, QuestionPage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import viewmodels.{DisplayMessage, YesNoPageViewModel}
import views.html.YesNoPageView

import scala.concurrent.ExecutionContext

class Question1Controller @Inject()(
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val view: YesNoPageView,
  val formProvider: YesNoPageFormProvider,
  val controllerComponents: MessagesControllerComponents
)(implicit val ec: ExecutionContext) extends YesNoPageController with ModeQuestionPage {

  override val page: UrlParams => QuestionPage[Boolean] = _ => Question1Page

  override val viewModel: Populate[YesNoPageViewModel] =
    Populate.const(YesNoPageViewModel(
      DisplayMessage("question1.title"),
      DisplayMessage("question1.heading"),
      _.fromMode(routes.Question1Controller.onSubmit)
    ))

  override val form: Populate[Form[Boolean]] =
    Populate.const(formProvider())
}