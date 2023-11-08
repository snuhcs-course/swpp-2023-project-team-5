from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import authentication_classes

from .serializers import FollowListSerializer
from myAuth.serializers import UserSerializer
from django.contrib.auth.models import User
from .models import Follow

class FollowList(APIView):
    def get(self, request):
        user = request.user
        user_data = UserSerializer(user).data

        # Filter and retrieve all follow objects for the current user (request.user)
        follows = Follow.objects.filter(user_id=user.id)
        follows_data = FollowListSerializer(follows, many=True).data

        response = {
            "user": user_data,
            "follows": follows_data
        }
        
        return Response(response, status=status.HTTP_200_OK)
    
class FollowBasic(APIView):
    def post(self, request, user_id):
        user = request.user

        # Check if the user is trying to follow themselves
        if user.id == user_id:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
        # Check if the user is trying to follow someone they already follow
        try:
            Follow.objects.get(user_id=user.id, follow_id=user_id)
            return Response(status=status.HTTP_400_BAD_REQUEST)
        except Follow.DoesNotExist:
            pass

        # Check if the user is trying to follow someone who doesn't exist
        try:
            follow_user = User.objects.get(id=user_id)
        except User.DoesNotExist:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
        # Create a new follow object
        follow = Follow(user_id=user, follow_id=follow_user)
        follow.save()

        return Response(status=status.HTTP_201_CREATED)
    
    def delete(self, request, user_id):
        # Check if the user is trying to unfollow someone who doesn't exist
        try:
            follow = Follow.objects.get(user_id=request.user.id, follow_id=user_id)
        except Follow.DoesNotExist:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        
        # Delete the follow object
        follow.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)