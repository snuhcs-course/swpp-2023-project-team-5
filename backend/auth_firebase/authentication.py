from rest_framework.authentication import BaseAuthentication
from auth_firebase import exceptions
from django.contrib.auth.models import User
import firebase_admin
from firebase_admin import credentials, auth
from django.conf import settings

"""SETUP FIREBASE CREDENTIALS"""
cred = credentials.Certificate({
    "type" : settings.FIREBASE_TYPE,
    "project_id" : settings.FIREBASE_PROJECT_ID,
    "private_key_id" : settings.FIREBASE_PRIVATE_KEY_ID,
    "private_key" : settings.FIREBASE_PRIVATE_KEY.replace("\\n", "\n"),
    "client_email" : settings.FIREBASE_CLIENT_EMAIL,
    "client_id" : settings.FIREBASE_CLIENT_ID,
    "auth_uri" : settings.FIREBASE_AUTH_URI,
    "token_uri" : settings.FIREBASE_TOKEN_URI,
    "auth_provider_x509_cert_url" : settings.FIREBASE_AUTH_PROVIDER_X509_CERT_URL,
    "client_x509_cert_url" : settings.FIREBASE_CLIENT_X509_CERT_URL,
    "universe_domain" : settings.FIREBASE_UNIVERSE_DOMAIN,
})
default_app = firebase_admin.initialize_app(cred)

"""FIREBASE AUTHENTICATION"""
class FirebaseAuthentication(BaseAuthentication):
    """override authenticate method and write our custom firebase authentication."""

    def getAuthorizationToken(self, request):
        """Get the authorization Token, It raise exception when no authorization Token is given"""
        auth_header = request.META.get("HTTP_AUTHORIZATION")
        if not auth_header:
            raise exceptions.NoAuthToken("No auth token provided")
        """Decoding the Token It rasie exception when decode failed."""
        id_token = auth_header.split(" ").pop()
        decoded_token = None
        
        try:
            decoded_token = auth.verify_id_token(id_token)
        except Exception:
            raise(exceptions.InvalidAuthToken("Invalid auth token"))
       
        """Return Nothing"""
        if not id_token or not decoded_token:
            return None
        
        """Get the uid of an user"""
        try:
            uid = decoded_token.get("uid")
        except Exception:
            raise(exceptions.FirebaseError())
        
        return uid

    def authenticate(self, request):
        
        """Get the uid of an user"""
        try:
            fb_uid = self.getAuthorizationToken(request)
        except Exception:
            raise(exceptions.FirebaseError())
        
        """Get or create the user"""
        user = User.objects.get(username=fb_uid)
        return (user, None)
