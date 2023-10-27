from rest_framework import serializers
import datetime

from .models import Plant

class PlantSerializer(serializers.ModelSerializer):
    height = serializers.IntegerField(default=0)
    status = serializers.CharField(default="alive")
    last_watered = serializers.DateField(default=datetime.date.today)

    class Meta:
        model = Plant
        fields = '__all__'