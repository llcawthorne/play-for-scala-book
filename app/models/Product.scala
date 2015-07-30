/* Models are a good place to start coding, since they are defined by the data
   and definition-independent of the rest of the code */
package models

// this is a warehouse app, so we need a model for our products
case class Product(ean: Long, name: String, description: String)

// We also need a data access object; we aren't going to a db now
// so we'll just initialize some static test data
object Product {

	// we're super lazy faking the dataset here
	var products = Set(
		Product(5010255079763L, "Paperclips Large", "Large Plain Pack of 1000"),
		Product(5018206244666L, "Giant Paperclips", "Giant Plain 51mm 100 pack"),
		Product(5018306332812L, "Paperclip Giant Plain", "Giant Plain Pack of 10000"),
		Product(5018306312913L, "No Tear Paper Clip", "No Tear Extra Large Pack of 1000"),
		Product(5018206244611L, "Zebra Paperclips", "Zebra Length 28mm Assorted 150 Pack")
	) 

	// here's our DAO methods; they'll be easy to point at persistent storage later
	def findAll = products.toList.sortBy(_.ean)     // finder function for product list <duh!>
  def findByEan(ean: Long) = products.find(_.ean == ean)  // uses Set's find method
  def add(product: Product) {
  	products = products + product 		// "save" the new product
  }
}
// they're waiting to chapter 5 to show us database access.  WTF