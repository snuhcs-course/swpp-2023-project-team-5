from django.urls import path

from . import views

urlpatterns = [
    path("", views.PlantView.as_view(), name="plant"),
    # path("user/{user_id}", views.getPlantUser, name="getPlantUser"),
    # path("{plant_id}", views.getPlant, name="getPlant"),
]