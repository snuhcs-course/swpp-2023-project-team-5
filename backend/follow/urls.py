from django.urls import path
from . import views

urlpatterns = [
    path("", views.FollowList.as_view(), name="FollowList"),
    path("<int:user_id>", views.FollowBasic.as_view(), name="FollowBasic"),
]