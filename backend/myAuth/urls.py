from django.urls import path
from . import views

urlpatterns = [
    path("signin", views.UserSignin.as_view(), name="UserSignin"),
    path("<int:user_id>", views.UserGet.as_view(), name="UserGet"),
    path("search", views.UserSearch.as_view(), name="UserSearch"),
    path("", views.UserBasic.as_view(), name="UserBasic"),
]
