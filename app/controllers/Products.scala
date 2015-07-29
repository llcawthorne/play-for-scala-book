package controllers

import play.api.mvc.{Action, Controller}
import models.Product

class Products extends Controller {
  def list = Action { implicit request =>
    // the request is the controller action, it causes us to
    // grab the product list
    val products = Product.findAll
    // then render the view template with it (and the implicit Lang val)
    Ok(views.html.products.list(products))
  }
}