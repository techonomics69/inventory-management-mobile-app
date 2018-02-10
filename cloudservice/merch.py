# Charles Jenkins (jenkinch)
# CS496 - Final Project

import webapp2
from google.appengine.ext import ndb
import db_models
import json

class Merch(webapp2.RequestHandler):
	def get(self, **kwargs):

		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# "if" retrieves specific Merch entity; "else" retrieves all Merch entities	
		if 'id' in kwargs:
			out = ndb.Key(db_models.Merch, int(kwargs['id'])).get().to_dict()
			self.response.write(json.dumps(out))
			self.response.write("\n")
		else:
			q = db_models.Merch.query()
			keys = q.fetch(keys_only = True)
			data = q.fetch()
			for i, j in zip(keys, data):
				results = {"key": i.id(), "name": j.name, "category": j.category, "size": j.size, "warehouse": j.warehouse.id(), "quantity": str(j.quantity), "barcode": j.barcode}
				self.response.write(json.dumps(results))
				self.response.write("\n")

	def post(self):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return
		
		# Creates a new Merch entity
		# name - merch name [required]
		# category - merch category
		# size - merch size
		# warehouse - merch warehouse [required] (if not provided, given 'unassigned' value of 1)
		# quantity - merch quantity [required]
		# barcode - merch barcode
		new_merch = db_models.Merch()
		name = self.request.get('name', default_value = None)
		category = self.request.get('category', default_value = None)
		size = self.request.get('size', default_value = None)
		warehouse = self.request.get('warehouse', default_value = None)
		quantity = self.request.get('quantity', default_value = None)
		barcode = self.request.get('barcode', default_value = None)
		
		if name:
			new_merch.name = name
		else:
			self.response.write('Invalid request. Merch name is required.\n')
			return
		
		if category:
			new_merch.category = category
		print new_merch.category
		
		if size:
			new_merch.size = size
		print new_merch.size
		
		if warehouse:
			if warehouse.isnumeric():
				new_merch.warehouse = ndb.Key(db_models.Warehouse, int(warehouse))
			else:
				self.response.write('Invalid request. Warehouse ID is non-numeric\n')
				return
		else:
			new_merch.warehouse = ndb.Key(db_models.Warehouse, 1)
		
		if quantity:
			if quantity.isnumeric():
				new_merch.quantity = int(quantity)
			else:
				self.response.write('Invalid request. Merch quantity is non-numeric\n')
				return
		else:
			self.response.write('Invalid request. Merch quantity is required.\n')
			return

		if barcode:
			new_merch.barcode = barcode
		print new_merch.barcode

		key = new_merch.put()
		out = new_merch.to_dict()
		self.response.write(json.dumps(out))
		self.response.write("\n")
		return

	def put(self, **kwargs):

		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Updates a specific Merch entity
		if 'id' in kwargs:
			merch = ndb.Key(db_models.Merch, int(kwargs['id'])).get()
			if not merch:
				self.response.status = 404
				self.response.status_message = 'Merch not found'
				return

			name = self.request.get('name', default_value = None)
			category = self.request.get('category', default_value = None)
			size = self.request.get('size', default_value = None)
			warehouse = self.request.get('warehouse', default_value = None)
			quantity = self.request.get('quantity', default_value = None)
			barcode = self.request.get('barcode', default_value = None)
			
			if name:
				merch.name = name
			
			if category:
				merch.category = category
			
			if size:
				merch.size = size
			
			if warehouse:
				self.response.status = 400
				self.response.write("Invalid request. Warehouse to merch association should be performed with the '/merch/<id>/warehouses/<id>' URL format.\n")
				return

			if quantity:
				if quantity.isnumeric():
					merch.quantity = int(quantity)
				else:
					self.response.write('Invalid request. Merch quantity is non-numeric\n')
					return

			if barcode:
				merch.barcode = barcode

			merch.put()

		self.response.write(json.dumps(merch.to_dict()))
		return	

	def delete(self, **kwargs):

		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Deletes a specific Merch entity
		if 'id' in kwargs:
			merch_to_delete = ndb.Key(db_models.Merch, int(kwargs['id']))
			merch_to_delete.delete()
			self.response.write("Deleted merch successfully.\n")
		else:
			self.response.write('Invalid request. Deleting all merch not an intended feature.\n')
			return
		
class MerchWarehouse(webapp2.RequestHandler):
	def put(self, **kwargs):

		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Associate a Warehouse entity with a Merch entity	
		if 'mid' in kwargs:
			merch = ndb.Key(db_models.Merch, int(kwargs['mid'])).get()
			if not merch:
				self.response.status = 404
				self.response.status_message = 'Merch not found'
				return
		if 'wid' in kwargs:
			warehouse = ndb.Key(db_models.Warehouse, int(kwargs['wid']))
			if not warehouse:
				self.response.status = 404
				self.response.status_message = 'Warehouse not found'
				return	
		if warehouse != merch.warehouse:
			merch.warehouse = warehouse
			merch.put()
		self.response.write(json.dumps(merch.to_dict()))
		return

class MerchSearch(webapp2.RequestHandler):
	def post(self):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Search/filter Merch by properties
		q = db_models.Merch.query()

		if self.request.get('name', None):
			q = q.filter(db_models.Merch.name == self.request.get('name'))
		if self.request.get('category', None):
			q = q.filter(db_models.Merch.category == self.request.get('category'))
		if self.request.get('size', None):
			q = q.filter(db_models.Merch.size == self.request.get('size'))
		if self.request.get('warehouse', None):
			if(self.request.get('warehouse').isnumeric()):
				q = q.filter(db_models.Merch.warehouse == ndb.Key(db_models.Warehouse, int(self.request.get('warehouse')) ))
			else:
				self.response.write("Invalid request. Warehouse ID is non-numeric.\n")
				return
		if self.request.get('quantity', None):
			if(self.request.get('quantity').isnumeric()):
				q = q.filter(db_models.Merch.quantity == int(self.request.get('quantity')))
			else:
				self.response.write("Invalid request. Quantity is non-numeric.\n")
				return
		if self.request.get('barcode', None):
			q = q.filter(db_models.Merch.barcode == self.request.get('barcode'))

		keys = q.fetch(keys_only = True)
		data = q.fetch()
		for i, j in zip(keys, data):
			results = {"key": i.id(), "name": j.name, "category": j.category, "size": j.size, "warehouse": j.warehouse.id(), "quantity": str(j.quantity), "barcode": j.barcode}
			self.response.write(json.dumps(results))
			self.response.write("\n")

class MerchSum(webapp2.RequestHandler):
	def get(self, **kwargs):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Sum all quantities of a specific Merch entity name
		q = db_models.Merch.query()

		s = str(kwargs['name'])

		s = s.replace ("_", " ")

		q = q.filter(db_models.Merch.name == s)

		data = q.fetch()
		totalQuantity = 0

		for j in data:
			totalQuantity += int(j.quantity)
		
		results = {"total quantity of "+s: totalQuantity}
		
		self.response.write(json.dumps(results))
		self.response.write("\n")
		if totalQuantity == 0:
			self.response.write('If summing a merch item with spaces in its name, replace the spaces with underscores.\n')