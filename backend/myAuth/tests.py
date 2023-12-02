from django.test import TestCase
from rest_framework.test import APIClient
from rest_framework import status
from django.contrib.auth.models import User
from .serializers import UserSerializer
from auth_firebase.authentication import FirebaseAuthentication

class UserGetTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', first_name='testuser', password='testpassword')
        self.client = APIClient()

    def test_get_user(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get(f'/api/user/{self.user.id}')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['user']['name'], 'testuser')

    def test_get_user_not_found(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get('/api/user/999')

        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)
        self.assertEqual(response.data['message'], 'User not found.')

class UserSearchTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword', first_name='testuser')
        self.client = APIClient()

    def test_search_users(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get('/api/user/search?username=test')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['users']), 1)

    def test_search_users_no_username(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get('/api/user/search')

        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(response.data['message'], 'Username not provided.')

    def test_search_users_not_found(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get('/api/user/search?username=nouserexist')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['users']), 0)
