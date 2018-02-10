# Charles Jenkins (jenkinch)
# CS496 - Final Project

import webapp2
from google.appengine.ext import ndb
import db_models
import json


class Warehouse(webapp2.RequestHandler):
	def get(self, **kwargs):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# "if" retrieves specific Warehouse entity; "else" retrieves all Warehouse entities
		if 'id' in kwargs:
			out = ndb.Key(db_models.Warehouse, int(kwargs['id'])).get().to_dict()
			self.response.write(json.dumps(out))
			self.response.write("\n")
		else:
			q = db_models.Warehouse.query()
			keys = q.fetch(keys_only = True)
			data = q.fetch()
			for i, j in zip(keys, data):
				results = {"key": i.id(), "name": j.name, "manager": j.manager, "email": j.email, "password": j.password}
				self.response.write(json.dumps(results))
				self.response.write("\n")

	def post(self):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Creates a new Warehouse entity
		# name - name of warehouse [required]
		# manager - manager's name
		# email - manager's email address
		# password - warehouse login password
		new_warehouse = db_models.Warehouse()
		name = self.request.get('name', default_value = None)
		manager = self.request.get('manager', default_value = None)
		email = self.request.get('email', default_value = None)
		password = self.request.get('password', default_value = None)

		if name:
			new_warehouse.name = name
		else:
			self.response.write('Invalid request. Name of warehouse is required.\n')
			return

		if manager:
			new_warehouse.manager = manager
		else:
			self.response.write('Invalid request. Manager of warehouse is required.\n')
			return

		if email:
			new_warehouse.email = email
		else:
			self.response.write('Invalid request. Email of warehouse manager is required.\n')
			return

		if password:
			new_warehouse.password = password
		else:
			self.response.write('Invalid request. Password for warehouse account is required.\n')
			return

		key = new_warehouse.put()
		out = new_warehouse.to_dict()
		self.response.write(json.dumps(out))
		self.response.write("\n")

	def put(self, **kwargs):
		
		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Updates a specific Warehouse entity
		if 'id' in kwargs:
			warehouse = ndb.Key(db_models.Warehouse, int(kwargs['id'])).get()
			if not warehouse:
				self.response.status = 404
				self.response.status_message = 'Merch not found'
				return

			name = self.request.get('name', default_value = None)
			manager = self.request.get('manager', default_value = None)
			email = self.request.get('email', default_value = None)
			password = self.request.get('password', default_value = None)
			
			if name:
				warehouse.name = name
			
			if manager:
				warehouse.manager = manager
			
			if email:
				warehouse.email = email

			if password:
				warehouse.password = password

			warehouse.put()

		self.response.write(json.dumps(warehouse.to_dict()))
		return	

	def delete(self, **kwargs):

		# Ensures request is for JSON
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = 'Not Acceptable, API only supports application/json MIME type'
			return

		# Deletes a specific Warehouse entity and clears any merch's associations
		if 'id' in kwargs:
			warehouse_to_delete = ndb.Key(db_models.Warehouse, int(kwargs['id']))
			warehouse_to_delete.delete()
			self.response.write("Warehouse deletion complete.\n")
			merch = db_models.Merch.query()
			merch_to_clean = merch.fetch()
			for i in merch_to_clean:
				if i.warehouse.id() == int(kwargs['id']):
					i.warehouse = ndb.Key(db_models.Warehouse, 1)
					i.put()
					self.response.write("Reference to deleted warehouse cleared.\n")
			return
		else:
			self.response.write('Invalid request. Deleting all warehouses not an intended feature.\n')
			return