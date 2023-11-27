from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import authentication_classes

from .serializers import UserSerializer
from django.contrib.auth.models import User

from auth_firebase.authentication import FirebaseAuthentication

class UserGet(APIView):
    def get(self, request, user_id):
        try:
            user = User.objects.get(id=user_id)
        except:
            return Response(status=status.HTTP_404_NOT_FOUND, data={"message": "User not found."})
        serializer = UserSerializer(user)
        return Response({"user": serializer.data})
    
class UserSearch(APIView):
    def get(self, request):
        username = request.GET.get('username', None)
        if username == None:
            return Response(status=status.HTTP_400_BAD_REQUEST, data={"message": "Username not provided."})
        try:
            user = User.objects.filter(first_name__icontains=username)
        except:
            return Response(status=status.HTTP_404_NOT_FOUND, data={"message": "User not found."})
        serializer = UserSerializer(user, many=True)
        return Response({"users": serializer.data})

@authentication_classes([])
class UserSignin(APIView):
    def post(self, request):
        
        try:
            fb_uid = FirebaseAuthentication.getAuthorizationToken(self, request)
        except:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

        serializer = UserSerializer(data=request.data)
        print(request.data)
        if serializer.is_valid(raise_exception=True):
            user = User.objects.create_user(username=fb_uid, first_name=serializer.validated_data['first_name'], email=serializer.validated_data['email'])
            return Response({"user": UserSerializer(user).data}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
class UserBasic(APIView):
    def get(self, request):
        user = request.user
        serializer = UserSerializer(user)
        return Response({"user": serializer.data})

    def put(self, request):
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