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

import base.SpecBase
import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import controllers.routes
import forms.mappings.Mappings
import forms.{DatePageFormProvider, IntPageFormProvider}
import models.{NormalMode, UrlParams, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.QuestionPage
import play.api.Application
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.inject.bind
import play.api.libs.json.JsPath
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.{DatePageViewModel, DisplayMessage, IntPageViewModel}
import views.html.{DatePageView, IntPageView}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

case object TestIntPage extends QuestionPage[Int] {
  override def path: JsPath = JsPath \ "testIntPage"
}

class TestIntPageController @Inject()(
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  val view: IntPageView
)(implicit val ec: ExecutionContext) extends IntPageController with ModeQuestionPage {
  override val form: Populate[Form[Int]] =
    Populate.const(TestIntPageController.form)

  override val page: UrlParams => QuestionPage[Int] = _ => TestIntPage
  override val viewModel: Populate[IntPageViewModel] = Populate.const(
    TestIntPageController.viewModel
  )
}

object TestIntPageController extends Mappings {

  val viewModel = IntPageViewModel(
    DisplayMessage("title"),
    DisplayMessage("heading"),
    _ => routes.IndexController.onPageLoad
  )

  val form = new IntPageFormProvider()()
}

class IntPageControllerSpec extends SpecBase with MockitoSugar {

  val form = TestIntPageController.form
  val viewModel = TestIntPageController.viewModel

  val validAnswer = 0

  lazy val onwardRoute = routes.IndexController.onPageLoad

  def controller(application: Application) =
    application.injector.instanceOf[TestIntPageController]

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest()

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest()
      .withFormUrlEncodedBody(
        "value" -> validAnswer.toString
      )

  def onPageLoad(application: Application, request: FakeRequest[AnyContentAsEmpty.type] = getRequest): Future[Result] =
    controller(application).onPageLoad(NormalMode).apply(request)

  def onSubmit(application: Application, request: FakeRequest[AnyContentAsFormUrlEncoded] = postRequest): Future[Result] =
    controller(application).onSubmit(NormalMode).apply(request)

  "IntPage Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = onPageLoad(application)

        val view = application.injector.instanceOf[IntPageView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, viewModel, UrlParams(NormalMode))(getRequest, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TestIntPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val view = application.injector.instanceOf[IntPageView]

        val result = onPageLoad(application)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), viewModel, UrlParams(NormalMode))(getRequest, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {

        val result = onSubmit(application)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest()
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[IntPageView]

        val result = onSubmit(application, request)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, viewModel, UrlParams(NormalMode))(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = onPageLoad(application)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = onSubmit(application)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
