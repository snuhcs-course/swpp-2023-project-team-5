# Your model app's urls.py

from django.urls import path
from . import views

app_name = 'model'

urlpatterns = [
    # ... other URL patterns for the model app ...
    path('predict_fcr/', views.predict_fcr_view, name='predict_fcr'),
]