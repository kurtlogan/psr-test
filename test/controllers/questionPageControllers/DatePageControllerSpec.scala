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
import forms.DatePageFormProvider
import forms.mappings.Mappings
import models.{Mode, NormalMode, UrlParams, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.QuestionPage
import play.api.Application
import play.api.data.{FieldMapping, Form}
import play.api.i18n.MessagesApi
import play.api.inject.bind
import play.api.libs.json.JsPath
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.{DatePageViewModel, DisplayMessage}
import views.html.DatePageView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.{ExecutionContext, Future}

case object TestDatePage extends QuestionPage[LocalDate] {
  override def path: JsPath = JsPath \ "testDatePage"
}

class TestDatePageController @Inject()(
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  val navigator: Navigator,
  val identify: IdentifierAction,
  val getData: DataRetrievalAction,
  val requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  val view: DatePageView
)(implicit val ec: ExecutionContext) extends DatePageController with ModeQuestionPage {
  override val form: Populate[Form[LocalDate]] =
    Populate.const(TestDatePageController.form)

  override val page: UrlParams => QuestionPage[LocalDate] = _ => TestDatePage
  override val viewModel: Populate[DatePageViewModel] = Populate.const(
    TestDatePageController.viewModel
  )
}

object TestDatePageController extends Mappings {

  val viewModel = DatePageViewModel(
    DisplayMessage("title"),
    DisplayMessage("legend"),
    DisplayMessage("hint"),
    _ => routes.IndexController.onPageLoad
  )

  val form = new DatePageFormProvider()()
}

class DatePageControllerSpec extends SpecBase with MockitoSugar {

  val viewModel = TestDatePageController.viewModel

  val form = TestDatePageController.form

  def onwardRoute = routes.IndexController.onPageLoad

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  def controller(application: Application) =
    application.injector.instanceOf[TestDatePageController]

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest()

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest()
      .withFormUrlEncodedBody(
        "value.day" -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year" -> validAnswer.getYear.toString
      )

  def onPageLoad(application: Application, request: FakeRequest[AnyContentAsEmpty.type] = getRequest): Future[Result] =
    controller(application).onPageLoad(NormalMode).apply(request)

  def onSubmit(application: Application, request: FakeRequest[AnyContentAsFormUrlEncoded] = postRequest): Future[Result] =
    controller(application).onSubmit(NormalMode).apply(request)

  "DatePage Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = onPageLoad(application)

        val view = application.injector.instanceOf[DatePageView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, viewModel, UrlParams(NormalMode))(FakeRequest(), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TestDatePage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val view = application.injector.instanceOf[DatePageView]

        val result = onPageLoad(application)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), viewModel, UrlParams(NormalMode))(FakeRequest(), messages(application)).toString
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

      val request =
        FakeRequest()
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DatePageView]

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
