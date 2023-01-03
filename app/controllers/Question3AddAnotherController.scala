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
import controllers.questionPageControllers.{BankAccountPageController, IndexedQuestionPage, Populate, YesNoPageController}
import forms.{BankAccountPageFormProvider, YesNoPageFormProvider}
import models.UrlParams.Indexed
import models.{BankAccount, UrlParams}
import navigation.Navigator
import pages.{Question3AddAnotherPage, Question3Page, QuestionPage}
import play.api.data.Form
import play.api.mvc.MessagesControllerComponents
import repositories.SessionRepository
import viewmodels.{BankAccountPageViewModel, DisplayMessage, YesNoPageViewModel}
import views.html.{BankAccountPageView, YesNoPageView}

import scala.concurrent.ExecutionContext

class Question3AddAnotherController @Inject()(
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val view: YesNoPageView,
  val formProvider: YesNoPageFormProvider,
  val controllerComponents: MessagesControllerComponents
)(implicit val ec: ExecutionContext) extends YesNoPageController with IndexedQuestionPage {

  override val page: UrlParams => QuestionPage[Boolean] = {
    case Indexed(_, pageNumber) => Question3AddAnotherPage(pageNumber)
    case _                                => Question3AddAnotherPage(1)
  }

  override val viewModel: Populate[YesNoPageViewModel] =
    Populate.const(YesNoPageViewModel(
      DisplayMessage("question3.add-another.title"),
      DisplayMessage("question3.add-another.heading"),
      _.fromIndexed(routes.Question3AddAnotherController.onSubmit)
    ))

  override val form: Populate[Form[Boolean]] =
    Populate.const(formProvider())
}