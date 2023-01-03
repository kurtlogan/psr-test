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

import base.SpecBase
import forms.YesNoPageFormProvider
import models.{CheckMode, Mode, NormalMode, UrlParams}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.{DisplayMessage, YesNoPageViewModel}
import views.html.YesNoPageView

import scala.concurrent.Future

trait YesNoControllerBaseSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val onPageLoadRoute: Mode => Call

  def aSimpleYesNoPage(form: Form[Boolean], viewModel: YesNoPageViewModel) = {

    "display correct view for NormalMode" in {

      val application = applicationBuilder().build()

      running(application) {

        val view = application.injector.instanceOf[YesNoPageView]
        val request = FakeRequest(GET, onPageLoadRoute(NormalMode).url)
        val result = route(application, request).value

        println(result.value)

        status(result) mustBe OK
        contentAsString(result) mustBe view(form, viewModel, UrlParams(NormalMode))(request, messages(application))
      }
    }

    "display correct view for CheckMode" in {

      val application = applicationBuilder().build()

      running(application) {

        val view = application.injector.instanceOf[YesNoPageView]
        val request = FakeRequest(GET, onPageLoadRoute(NormalMode).url)
        val result = route(application, request).value

        status(result) mustBe OK
        contentAsString(result) mustBe view(form, viewModel, UrlParams(CheckMode))(request, messages(application))
      }
    }
  }

}

class Question1ControllerSpec extends YesNoControllerBaseSpec {

  val viewModel =
    YesNoPageViewModel(
      DisplayMessage("question1.title"),
      DisplayMessage("question1.header"),
      _.fromMode(routes.Question1Controller.onSubmit)
    )

  val form = new YesNoPageFormProvider()()

  override val onPageLoadRoute: Mode => Call = routes.Question1Controller.onPageLoad

  "question1Controller" - {

    behave like aSimpleYesNoPage(form, viewModel)
  }
}