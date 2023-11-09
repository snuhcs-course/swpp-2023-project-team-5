from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Plant(models.Model):
    name = models.CharField(max_length=100)
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)
    birthday = models.DateField(auto_now=True)
    height = models.IntegerField()
    status = models.CharField(max_length=100)
    avatar = models.IntegerField(default=0)
    last_watered = models.DateField()
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
