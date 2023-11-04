from rest_framework import serializers
from django.contrib.auth.models import User


class UserSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(read_only=True)
    name = serializers.CharField(source="first_name")
    email = serializers.EmailField()

    class Meta:
        model = User
        fields = ('id', 'name', 'email')

    def validate(self, data):
        name = data.get('first_name')
        email = data.get('email')

        if not name or not email:
            raise serializers.ValidationError("Both name and email must be provided.")

        return data
