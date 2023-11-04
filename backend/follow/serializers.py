from rest_framework import serializers
from myAuth.serializers import UserSerializer
from .models import Follow

class FollowListSerializer(serializers.ModelSerializer):
    follow = UserSerializer(source='follow_id', read_only=True)

    class Meta:
        model = Follow
        fields = ('follow',)
    
    def to_representation(self, instance):
        return super().to_representation(instance).get('follow')