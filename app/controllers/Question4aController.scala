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
import controllers.questionPageControllers.{BankAccountPageController, IntPageController, ModeQuestionPage, Populate}
import forms.{BankAccountPageFormProvider, IntPageFormProvider}
import models.UrlParams.Indexed
import models.{BankAccount, UrlParams}
import navigation.Navigator
import pages.{Question3Page, Question4aPage, QuestionPage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import viewmodels.{BankAccountPageViewModel, DisplayMessage, IntPageViewModel}
import views.html.{BankAccountPageView, IntPageView}

import scala.concurrent.ExecutionContext

class Question4aController @Inject()(
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val view: IntPageView,
  val formProvider: IntPageFormProvider,
  val controllerComponents: MessagesControllerComponents
)(implicit val ec: ExecutionContext) extends IntPageController with ModeQuestionPage {

  override val page: UrlParams => QuestionPage[Int] = _ => Question4aPage

  override val viewModel: Populate[IntPageViewModel] =
    Populate.const(IntPageViewModel(
      DisplayMessage("question4a.title"),
      DisplayMessage("question4a.heading"),
      _.fromMode(routes.Question4aController.onSubmit)
    ))

  override val form: Populate[Form[Int]] =
    Populate.const(formProvider())
}