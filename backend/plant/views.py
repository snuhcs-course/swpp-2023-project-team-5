from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from django.contrib.auth.models import User
from .models import Plant
from .serializer import PlantSerializer

class PlantBasic(APIView):
    def post(self, request):
        user = request.user

        data = request.data
        request.data._mutable  = True
        request.data['user_id'] = user.id
        request.data._mutable = False
        
        serializer = PlantSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response({"plant": serializer.data})
        else:
            return Response({"plant": serializer.errors})
        
    def put(self, request):
        user = request.user

        plant_id = request.data.get('plant_id', None)
        try:
            plant = Plant.objects.get(id=plant_id)
        except:
            return Response({'plant_id': 'Plant does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        if user.id != plant.user_id:
            return Response({'plant_id': 'Plant does not belong to user.'}, status=status.HTTP_400_BAD_REQUEST)

        serializer = PlantSerializer(plant, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response({"plant": serializer.data})
        else:
            return Response({"plant": serializer.errors})
    
    def delete(self, request):
        user = request.user
        plant_id = request.data.get('plant_id', None)
        try:
            plant = Plant.objects.get(id=plant_id)
        except:
            return Response({'plant_id': 'Plant does not exist.'}, status=status.HTTP_400_BAD_REQUEST)
        
        if user.id != plant.user_id:
            return Response({'plant_id': 'Plant does not belong to user.'}, status=status.HTTP_400_BAD_REQUEST)

        plant.delete()
        return Response({"plant": "deleted"})

class PlantUser(APIView):
    def get(self, request, user_id):
        plants = Plant.objects.filter(user_id=user_id)
        serializer = PlantSerializer(plants, many=True)
        return Response({"plants": serializer.data})

class PlantGet(APIView):
    def get(self, request, plant_id):
        try:
            plant = Plant.objects.get(id=plant_id)
        except:
            return Response({'plant_id': 'Plant does not exist.'}, status=status.HTTP_400_BAD_REQUEST)

        serializer = PlantSerializer(plant)
        return Response({"plant": serializer.data})
