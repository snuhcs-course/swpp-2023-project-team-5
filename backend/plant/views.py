from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from django.contrib.auth.models import User
from .models import Plant
from .serializer import PlantSerializer

class PlantUser(APIView):
    def get(self, request, user_id):
        plants = Plant.objects.filter(user_id=user_id)
        serializer = PlantSerializer(plants, many=True)
        return Response({"plants": serializer.data})

class PlantGet(APIView):
    def get(self, request, plant_id):
        plant = Plant.objects.get(id=plant_id)
        serializer = PlantSerializer(plant)
        return Response({"plant": serializer.data})