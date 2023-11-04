from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Follow(models.Model):
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)
    follow_id = models.ForeignKey(User, on_delete=models.CASCADE, related_name='follow_id')
    
    def __str__(self):
        return str(self.user_id) + " follows " + str(self.follow_id)
    
    class Meta:
        unique_together = ('user_id', 'follow_id')
