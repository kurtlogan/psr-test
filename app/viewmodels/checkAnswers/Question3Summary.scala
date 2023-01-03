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

package viewmodels.checkAnswers

import controllers.routes
import models.{BankAccount, CheckMode, UserAnswers}
import pages.Question3Page
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

object Question3Summary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[List[SummaryListRow]] =
    answers.get[Map[String, BankAccount]](Question3Page.parentPath).map {
      answer =>
        answer.toList.map { case (key, value) =>
          SummaryListRow(
            key = Key(Text(messages("question3.checkYourAnswersLabel", key))),
            value = Value(Text(value.toString)),
            actions = Some(Actions(
              items = Seq(
                ActionItem(href = routes.Question3Controller.onPageLoad(CheckMode, key.toInt).url, Text(messages("site.change")))
              )))
          )
        }
    }
}