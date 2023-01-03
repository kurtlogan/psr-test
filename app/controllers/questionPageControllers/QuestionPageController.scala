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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{Mode, UrlParams}
import navigation.Navigator
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{Reads, Writes}
import play.api.mvc.{Action, AnyContent, Request}
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

abstract class QuestionPageController[ViewModel, FormData: Reads : Writes] extends FrontendBaseController with I18nSupport {
  val sessionRepository: SessionRepository
  val navigator: Navigator
  val identify: IdentifierAction
  val getData: DataRetrievalAction
  val requireData: DataRequiredAction
  implicit val ec: ExecutionContext

  def createView(form: Form[FormData], viewModel: ViewModel, params: UrlParams)(implicit request: Request[_], messages: Messages): Html

  val page: UrlParams => QuestionPage[FormData]
  val viewModel: Populate[ViewModel]
  val form: Populate[Form[FormData]]

  protected def onPageLoad(params: UrlParams): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        form <- form.run(request)
        viewModel <- viewModel.run(request)
      } yield {
        val preparedForm = page(params).get(request.userAnswers) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(createView(preparedForm, viewModel, params))
      }
  }

  protected def onSubmit(params: UrlParams): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      for {
        form <- form.run(request)
        viewModel <- viewModel.run(request)
        result <- form.bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(createView(formWithErrors, viewModel, params))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(page(params).set(request.userAnswers, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(page(params), params.mode, updatedAnswers))
        )
      } yield {
        result
      }
  }
}

trait ModeQuestionPage { self: QuestionPageController[_, _] =>

  def onPageLoad(mode: Mode): Action[AnyContent] = onPageLoad(UrlParams(mode))

  def onSubmit(mode: Mode): Action[AnyContent] = onSubmit(UrlParams(mode))
}

trait IndexedQuestionPage { self: QuestionPageController[_, _] =>

  def onPageLoad(mode: Mode, index: Int): Action[AnyContent] = onPageLoad(UrlParams(mode, index))

  def onSubmit(mode: Mode, index: Int): Action[AnyContent] = onSubmit(UrlParams(mode, index))
}