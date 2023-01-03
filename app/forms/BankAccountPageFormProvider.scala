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

package forms

import forms.mappings.Mappings
import models.BankAccount
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.Constraint

import javax.inject.Inject

class BankAccountPageFormProvider @Inject() extends Mappings {

  def apply(
             bankNameRequiredKey: String = "bankName.error.required",
             sortCodeRequiredKey: String = "sortCode.error.required",
             sortCodeInvalidKey: String = "sortCode.error.invalid",
             accountNumberRequiredKey: String = "accountNumber.error.required",
             accountNumberInvalidKey: String = "accountNumber.error.invalid"
           ): Form[BankAccount] =
    Form(
      "value" -> mapping(
        "bankName" -> text(bankNameRequiredKey),
        "sortCode" -> text(sortCodeRequiredKey)
          .verifying(regexp("[0-9]{2}\\s?-?[0-9]{2}\\s?-?[0-9]{2}", sortCodeInvalidKey)),
        "accountNumber" -> text(accountNumberRequiredKey)
          .verifying(regexp("[0-9]{4}\\s?[0-9]{4}\\s?[0-9]{4}\\s?[0-9]{4}", accountNumberInvalidKey))
      )(BankAccount.apply)(BankAccount.unapply)
    )
}
