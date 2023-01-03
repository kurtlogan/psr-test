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
import controllers.questionPageControllers.{BankAccountPageController, IndexedQuestionPage, Populate}
import forms.BankAccountPageFormProvider
import models.UrlParams.Indexed
import models.{BankAccount, UrlParams}
import navigation.Navigator
import pages.{Question3Page, QuestionPage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import viewmodels.{BankAccountPageViewModel, DisplayMessage}
import views.html.BankAccountPageView

import scala.concurrent.ExecutionContext

class Question3Controller @Inject()(
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val view: BankAccountPageView,
  val formProvider: BankAccountPageFormProvider,
  val controllerComponents: MessagesControllerComponents
)(implicit val ec: ExecutionContext) extends BankAccountPageController with IndexedQuestionPage {

  override val page: UrlParams => QuestionPage[BankAccount] = {
    case Indexed(_, pageNumber) => Question3Page(pageNumber)
    case _                      => Question3Page(1)
  }

  override val viewModel: Populate[BankAccountPageViewModel] =
    Populate.const(BankAccountPageViewModel(
      DisplayMessage("question3.title"),
      DisplayMessage("question3.bankName"),
      DisplayMessage("question3.sortCode"),
      DisplayMessage("question3.accountNumber"),
      _.fromIndexed(routes.Question3Controller.onSubmit)
    ))

  override val form: Populate[Form[BankAccount]] =
    Populate.const(formProvider())
}