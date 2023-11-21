# views.py

from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .src.main import predict_fcr_status  # Adjusted import statement
import json
import os

@csrf_exempt
def predict_fcr_view(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body.decode('utf-8'))

            # Construct the full path to the data file
            base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
            data_path = os.path.join(base_dir, 'data', 'FCR_Data.xlsx')

            prediction = predict_fcr_status(data, data_path)
            return JsonResponse({'fcr_status': prediction})
        except Exception as e:
            return JsonResponse({'error': str(e)}, status=400)
    else:
        return JsonResponse({'error': 'This endpoint supports only POST requests.'}, status=405)
