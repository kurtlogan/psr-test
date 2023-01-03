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

package models

import models.UrlParams.Indexed
import play.api.mvc.JavascriptLiteral

sealed trait UrlParams { self =>

  val mode: Mode

  def fromMode[A](f: Mode => A): A = f(mode)

  def fromIndexed[A](f: (Mode, Int) => A): A = self match {
    case Indexed(mode, page) => f(mode, page)
  }
}

object UrlParams {

  def apply(mode: Mode): UrlParams = ModeOnly(mode)
  def apply[A](mode: Mode, pageNumber: Int): UrlParams = Indexed(mode, pageNumber)

  case class ModeOnly(mode: Mode) extends UrlParams
  case class Indexed(mode: Mode, page: Int) extends UrlParams

  implicit val jsLiteral: JavascriptLiteral[UrlParams] = new JavascriptLiteral[UrlParams] {
    override def to(value: UrlParams): String = value match {
      case ModeOnly(mode) => Mode.jsLiteral.to(mode)
      case Indexed(mode, id) => (Mode.jsLiteral.to(mode), id.toString).toString
    }
  }
}