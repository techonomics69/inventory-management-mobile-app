# Charles Jenkins (jenkinch)
# CS496 - Final Project

import webapp2
from google.appengine.api import oauth


app = webapp2.WSGIApplication([
	
	], debug = True)

app.router.add(webapp2.Route(r'/warehouses', 'warehouse.Warehouse'))
app.router.add(webapp2.Route(r'/warehouses/<id:[0-9]+><:/?>', 'warehouse.Warehouse'))
app.router.add(webapp2.Route(r'/merch/search', 'merch.MerchSearch'))
app.router.add(webapp2.Route(r'/merch/sum/<name:[a-zA-Z0-9_]+><:/?>', 'merch.MerchSum'))
app.router.add(webapp2.Route(r'/merch', 'merch.Merch'))
app.router.add(webapp2.Route(r'/merch/<id:[0-9]+><:/?>', 'merch.Merch'))
app.router.add(webapp2.Route(r'/merch/<mid:[0-9]+>/warehouses/<wid:[0-9]+><:/?>', 'merch.MerchWarehouse'))
	
