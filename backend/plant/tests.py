from django.test import TestCase
from django.contrib.auth.models import User
from rest_framework.test import APIClient
from rest_framework import status
from .models import Plant

class PlantBasicTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()

    def test_create_plant(self):
        self.client.force_authenticate(user=self.user)
        data = {
            'name': 'Plant 1',
            'image': 'test.jpg',
        }

        response = self.client.post('/api/plant/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['plant']['name'], 'Plant 1')
        self.assertEqual(Plant.objects.count(), 1)

    def test_create_plant_invalid(self):
        self.client.force_authenticate(user=self.user)
        data = {
        }

        response = self.client.post('/api/plant/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(Plant.objects.count(), 0)

    def test_update_plant(self):
        self.client.force_authenticate(user=self.user)
        plant = Plant.objects.create(name='Plant 1', user_id=self.user, height = 10, last_watered = '2023-12-01')

        data = {
            'plant_id': plant.id,
            'name': 'Plant 2',
        }

        response = self.client.put('/api/plant/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['plant']['name'], 'Plant 2')
        self.assertEqual(Plant.objects.count(), 1)

    def test_delete_plant(self):
        self.client.force_authenticate(user=self.user)
        plant = Plant.objects.create(name='Plant 1', user_id=self.user, height = 10, last_watered = '2023-12-01')

        data = {
            'plant_id': plant.id,
        }

        response = self.client.delete('/api/plant/', data, format='multipart')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(Plant.objects.count(), 0)

class PlantUserTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.client = APIClient()

    def test_get_user_plants(self):
        self.client.force_authenticate(user=self.user)

        # Create some plants associated with the user
        Plant.objects.create(name='Plant 1', user_id=self.user, height = 10, last_watered = '2023-12-01')
        Plant.objects.create(name='Plant 2', user_id=self.user, height = 10, last_watered = '2023-12-01')

        response = self.client.get(f'/api/plant/user/{self.user.id}')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['plants']), 2)

class PlantGetTest(TestCase):
    def setUp(self):
        self.user = User.objects.create_user(username='testuser', password='testpassword')
        self.plant = Plant.objects.create(name='Plant 1', user_id=self.user, height = 10, last_watered = '2023-12-01')
        self.client = APIClient()

    def test_get_plant(self):
        self.client.force_authenticate(user=self.user)
        response = self.client.get(f'/api/plant/{self.plant.id}')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['plant']['name'], 'Plant 1')
