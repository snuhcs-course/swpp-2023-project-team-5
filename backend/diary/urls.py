from django.urls import path

from . import views

urlpatterns = [
    path("", views.DiaryBasic.as_view(), name="diary"),
    path("user/<int:user_id>", views.DiaryUser.as_view(), name="getDiaryUser"),
    path("<int:user_id>", views.DiaryGet.as_view(), name="getDiary"),
]
