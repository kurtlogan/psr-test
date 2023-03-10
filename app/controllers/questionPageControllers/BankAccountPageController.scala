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

import models.{BankAccount, UrlParams}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Request
import play.twirl.api.Html
import viewmodels.BankAccountPageViewModel
import views.html.BankAccountPageView

abstract class BankAccountPageController extends QuestionPageController[BankAccountPageViewModel, BankAccount] {
  val view: BankAccountPageView

  def createView(form: Form[BankAccount], viewModel: BankAccountPageViewModel, params: UrlParams)(implicit request: Request[_], messages: Messages): Html =
    view(form, viewModel, params)(request, messages)

}