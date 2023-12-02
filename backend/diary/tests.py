from django.test import TestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from .models import Diary
from .serializers import DiarySerializer
from s3uploader.uploader import S3ImageUploader

class DiaryBasicTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()

    def test_create_diary(self):
        self.client.force_authenticate(user=self.user)
        data = {
            'title': 'Sample Diary',
            'content': 'A sample diary content',
        }

        response = self.client.post('/api/diary/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(Diary.objects.count(), 1)

    def test_update_diary(self):
        self.client.force_authenticate(user=self.user)

        diary = Diary.objects.create(title='Sample Diary', user_id=self.user)

        data = {
            'diary_id': diary.id,
            'title': 'Sample Diary Updated',
            'content': 'A sample diary content',
        }
        
        response = self.client.put('/api/diary/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(Diary.objects.count(), 1)
        self.assertEqual(Diary.objects.get().title, 'Sample Diary Updated')

    def test_get_diary(self):
        self.client.force_authenticate(user=self.user)

        Diary.objects.create(title='Sample Diary', user_id=self.user)
        
        response = self.client.get('/api/diary/', format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['diaries']), 1)

    def test_delete_diary(self):
        self.client.force_authenticate(user=self.user)

        diary = Diary.objects.create(title='Sample Diary', user_id=self.user)

        data = {
            'diary_id': diary.id,
        }
        
        response = self.client.delete('/api/diary/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(Diary.objects.count(), 0)

class DiaryUserTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()

    def test_get_user_diaries(self):
        self.client.force_authenticate(user=self.user)

        Diary.objects.create(title='Diary 1', user_id=self.user)
        Diary.objects.create(title='Diary 2', user_id=self.user)

        response = self.client.get(f'/api/diary/user/{self.user.id}')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['diaries']), 2)

class DiaryGetTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.diary = Diary.objects.create(title='Test Diary', user_id=self.user)
        self.client = APIClient()

    def test_get_diary(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get(f'/api/diary/{self.diary.id}')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['diary']['title'], 'Test Diary')
