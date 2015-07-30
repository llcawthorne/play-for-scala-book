package controllers

import models.Product
import play.api.data._
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import play.api.mvc.{Action, Controller, Flash}

class Products extends Controller {
  // We're going to use this Form several places, so defining it as a field
  // here, then passing it to the views makes sense
  private val productForm: Form[Product] = Form(
    mapping ( // fields defined & associated with constraints from data.Forms._
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
      )(Product.apply)(Product.unapply)   // functions to map between Form and Model
    )  // we can use the shortcut with apply/unapply here because our form's fields map directly
  // to the Product class's fields.  otherwise, we'ld have to define our own functions to map

  def list = Action { implicit request =>
    // the request is the controller action, it causes us to
    // grab the product list
    val products = Product.findAll
    // then render the view template with it (and the implicit Lang val)
    Ok(views.html.products.list(products))
  }

  def show(ean: Long) = Action { implicit request =>
    Product.findByEan(ean).map { product =>     // findByEan (defined in Product model)
      // map is used because the product is wrapped in an Option, either we:
      Ok(views.html.products.details(product))  // render the product
    }.getOrElse(NotFound)  // or return 404 if product doesn't exist
  }

  def newProduct = Action { implicit request =>
    // load error values in flash for correction if they exist, otherwise blank
    val form = if (request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm

    Ok(views.html.products.editProduct(form))   // render it!
  }
  
  // save lets us add new products to the Model using the form defined below as productForm
  def save = Action { implicit request =>
    // bindFromRequest binds the parameters to form fields with the same names and validates
    val newProductForm = productForm.bindFromRequest()

    // folding calls hasErrors if validation fails, success if validation passes
    newProductForm.fold(
      hasErrors = { form =>
        // take advantage of our flash scope to pass along errors with this redirect
        // all SimpleResult (Ok, Redirect, etc) types have a flashing method to use
        Redirect(routes.Products.newProduct()).
          flashing(Flash(form.data) + 
            ("error" -> "Please correct the errors in the form."))
            //("error" -> Messages("validation.errors")))   // nope, bedtime
      },
      success = { newProduct =>
        Product.add(newProduct)
        val message = "Successfully added new product!"
        Redirect(routes.Products.show(newProduct.ean)).flashing("success" -> message)
      }
    )
  }
}