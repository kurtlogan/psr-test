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
import controllers.questionPageControllers.{DatePageController, ModeQuestionPage, Populate}
import forms.DatePageFormProvider
import models.UrlParams
import navigation.Navigator
import pages.{Question1Page, Question2Page, QuestionPage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import viewmodels.{DatePageViewModel, DisplayMessage}
import views.html.DatePageView

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class Question2Controller @Inject()(
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val view: DatePageView,
  val formProvider: DatePageFormProvider,
  val controllerComponents: MessagesControllerComponents
)(implicit val ec: ExecutionContext) extends DatePageController with ModeQuestionPage {

  override val page: UrlParams => QuestionPage[LocalDate] = _ => Question2Page

  override val viewModel: Populate[DatePageViewModel] =
    Populate.const(DatePageViewModel(
      DisplayMessage("question2.title"),
      DisplayMessage("question2.heading"),
      DisplayMessage("question2.hint"),
      _.fromMode(routes.Question2Controller.onSubmit)
    ))

  override val form: Populate[Form[LocalDate]] =
    Populate.fromRequest { r =>
      val form = formProvider()
      r.userAnswers.get(Question1Page)
        .filter(identity)
        .fold(form)(_ => form.fill(r.activeYear))
    }
}