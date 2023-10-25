from rest_framework.views import APIView
from rest_framework.response import Response

from .models import Plant
from .serializer import PlantSerializer

class PlantView(APIView):
    def get(self, request):
        plants = Plant.objects.all()
        serializer = PlantSerializer(plants, many=True)
        return Response({"plants": serializer.data})