## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
import logging
import random
from concurrent.futures import ThreadPoolExecutor, as_completed
import requests
from django.http import JsonResponse, HttpResponse

logger = logging.getLogger(__name__)


def healthcheck(request):
    logger.info('/ called')
    return HttpResponse("Success - /")

def http_call(request):
    url = "https://www.amazon.com"
    try:
        response = requests.get(url)
        status_code = response.status_code
        logger.info("outgoing-http-call status code: " + str(status_code))
    except Exception as e:
        logger.error("Could not complete http request:" + str(e))
    return HttpResponse('', status=200)

def status_a(request, code):
    logger.info(f'Service A returning status code: {code}')
    return HttpResponse('', status=code)

def status_b(request, code):
    try:
        requests.get(f'http://localhost:8081/status/{code}')
    except Exception:
        pass
    logger.info(f'Service A requested status code {code} from Service B')
    return HttpResponse('', status=200)

def status_c(request, code):
    try:
        requests.get(f'http://localhost:8081/status/c/{code}')
    except Exception:
        pass
    logger.info(f'Service A requested status code {code} from Service C through Service B')
    return HttpResponse('', status=200)

def latency_c(request, seconds):
    try:
        requests.get(f'http://localhost:8081/latency/c/{seconds}')
    except Exception:
        pass
    logger.info(f'Service A requested {seconds} second latency from Service C through Service B')
    return HttpResponse('', status=200)


def _make_calls_on_thread(thread_id, amount):
    """Worker function that makes `amount` calls sequentially on this thread."""
    thread_results = {'completed': 0, 'failed': 0, 'status_200_requested': 0, 'status_500_requested': 0}
    
    for i in range(amount):
        status = 200 if random.random() < 0.75 else 500
        url = f'http://localhost:8081/status/{status}'
        try:
            requests.get(url, timeout=10)
            thread_results['completed'] += 1
        except Exception:
            thread_results['failed'] += 1
        
        if status == 200:
            thread_results['status_200_requested'] += 1
        else:
            thread_results['status_500_requested'] += 1
    
    return thread_results


def multi_b(request, amount):
    """
    Spawns 10 threads, each making `amount` HTTP calls to Service B concurrently.
    Each call randomly targets /status/200 (75%) or /status/500 (25%).
    Total calls = amount * 10.
    """
    results = {'total': amount * 10, 'completed': 0, 'failed': 0, 'status_200_requested': 0, 'status_500_requested': 0}
    
    with ThreadPoolExecutor(max_workers=10) as executor:
        futures = [executor.submit(_make_calls_on_thread, thread_id, amount) for thread_id in range(10)]
        
        for future in as_completed(futures):
            thread_results = future.result()
            results['completed'] += thread_results['completed']
            results['failed'] += thread_results['failed']
            results['status_200_requested'] += thread_results['status_200_requested']
            results['status_500_requested'] += thread_results['status_500_requested']
    
    return JsonResponse(results)
