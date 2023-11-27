from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from django.contrib.auth.models import User
from .models import Diary
from .serializers import DiarySerializer

from s3uploader.uploader import S3ImageUploader

class DiaryBasic(APIView):
    def get(self, request):
        user = request.user
        diaries = Diary.objects.filter(user_id=user.id)
        serializer = DiarySerializer(diaries, many=True)
        return Response({"diaries": serializer.data})

    def post(self, request):
        user = request.user

        auth_data = {'user_id': user.id}

        request.POST._mutable  = True
        request.data.update(auth_data)

        img = request.FILES.get("image")
        if img != None:
            url = S3ImageUploader(img).upload()
            request.data.update({'image_src': url})

        serializer = DiarySerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response({"diary": serializer.data})
        else:
            return Response({"diary": serializer.errors})
        
    def put(self, request):
        user = request.user

        diary_id = request.data.get('diary_id', None)
        try:
            diary = Diary.objects.get(id=diary_id)
        except:
            return Response({'diary_id': 'Diary does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        if user.id != diary.user_id.id:
            return Response({'diary_id': 'Diary does not belong to user.'}, status=status.HTTP_400_BAD_REQUEST)
        
        request.POST._mutable  = True

        img = request.FILES.get("image")
        if img != None:
            url = S3ImageUploader(img).upload()
            request.data.update({'image_src': url})

        serializer = DiarySerializer(diary, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response({"diary": serializer.data})
        else:
            return Response({"diary": serializer.errors})
    
    def delete(self, request):
        user = request.user
        diary_id = request.data.get('diary_id', None)
        try:
            diary = Diary.objects.get(id=diary_id)
        except:
            return Response({'diary_id': 'Diary does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        if user.id != diary.user_id:
            return Response({'diary_id': 'Diary does not belong to user.'}, status=status.HTTP_400_BAD_REQUEST)

        diary.delete()
        return Response({"diary": "deleted"})

class DiaryUser(APIView):
    def get(self, request, user_id):

        filter_options = {'user_id': user_id}

        if request.user.id != user_id:
            filter_options.update({'is_private': False})
        
        diaries = Diary.objects.filter(filter_options)
        serializer = DiarySerializer(diaries, many=True)
        return Response({"diaries": serializer.data})

class DiaryGet(APIView):
    def get(self, request, diary_id):
        try:
            diary = Diary.objects.get(id=diary_id)
        except:
            return Response({'diary_id': 'Diary does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        if request.user.id != diary.user_id.id and diary.is_private:
            return Response({'diary_id': 'Diary does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        serializer = DiarySerializer(diary)
        return Response({"diary": serializer.data})
