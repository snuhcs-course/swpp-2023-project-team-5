from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import authentication_classes

from .serializers import UserSerializer
from django.contrib.auth.models import User

from auth_firebase.authentication import FirebaseAuthentication

class FollowList(APIView):
    def get(self, request):
        user = request.user

        
        serializer = UserSerializer(user)
        return Response({"user": serializer.data})
    
class FollowBasic(APIView):
    def post(self, request):
        user = request.user
        serializer = UserSerializer(user, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response({"user": serializer.data})
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
    def delete(self, request):
        user = request.user
        user.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)