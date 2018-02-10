# Charles Jenkins (jenkinch)
# CS496 - Final Project

from google.appengine.ext import ndb


class Model(ndb.Model):
	def to_dict(self):
		d = super(Model, self).to_dict()
		d['key'] = self.key.id()
		return d
	
class Merch(Model):
	name = ndb.StringProperty(required = True)
	category = ndb.StringProperty()
	size = ndb.StringProperty()
	warehouse = ndb.KeyProperty(required = True)
	quantity = ndb.IntegerProperty(required = True)
	barcode = ndb.StringProperty()
	
	def to_dict(self):
		d = super(Merch, self).to_dict()
		d['warehouse'] = d['warehouse'].id()
		return d
		
class Warehouse(Model):
	name = ndb.StringProperty(required = True)
	manager = ndb.StringProperty(required = True)
	email = ndb.StringProperty(required = True)
	password = ndb.StringProperty(required = True)
	