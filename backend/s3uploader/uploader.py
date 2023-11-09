from django.conf import settings
import boto3
import uuid

aws_access_key_id = settings.AWS_ACCESS_KEY_ID
aws_secret_access_key = settings.AWS_SECRET_ACCESS_KEY
aws_storage_bucket_name = settings.AWS_STORAGE_BUCKET_NAME
aws_s3_region_name = settings.AWS_S3_REGION_NAME

class S3ImageUploader:
    def __init__(self, file):
        self.file = file

    def upload(self):
        s3_client = boto3.client('s3',
            aws_access_key_id=aws_access_key_id,
            aws_secret_access_key=aws_secret_access_key,
            region_name=aws_s3_region_name,
        )
        i = str(uuid.uuid4())
        response = s3_client.upload_fileobj(self.file, aws_storage_bucket_name, i)
        return f'https://{aws_storage_bucket_name}.s3.{aws_s3_region_name}.amazonaws.com/{i}'