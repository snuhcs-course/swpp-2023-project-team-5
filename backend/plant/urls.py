from django.urls import path

from . import views

urlpatterns = [
    path("", views.PlantBasic.as_view(), name="plant"),
    path("user/<int:user_id>", views.PlantUser.as_view(), name="getPlantUser"),
    path("<int:plant_id>", views.PlantGet.as_view(), name="getPlant"),
]