package views

import base.SpecBase
import controllers.routes
import forms.BankAccountPageFormProvider
import models.{NormalMode, UrlParams}
import org.jsoup.Jsoup
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.running
import viewmodels.{BankAccountPageViewModel, DisplayMessage}
import views.html.BankAccountPageView

class BankAccountPageViewSpec extends SpecBase {


  running(_ => applicationBuilder()) { implicit app =>

    val view = app.injector.instanceOf[BankAccountPageView]

    implicit val request = FakeRequest()
    implicit val mess = messages(app)

    "test 1" in {

      val form = new BankAccountPageFormProvider()
      val viewModel = BankAccountPageViewModel(
        DisplayMessage("value 1"),
        DisplayMessage("value 2"),
        DisplayMessage("value 3"),
        DisplayMessage("value 4"),
        _ => Call("GET", "/new-page")
      )

      val failingForm = form("no bank name").bind(Map("sortCode" -> "1234"))

      val result = view(failingForm, viewModel, UrlParams(NormalMode))
      Jsoup.parse(result.toString()).body() must include("value 1")
      Jsoup.parse(result.toString()).body() must include("no bank name")
    }
  }

}