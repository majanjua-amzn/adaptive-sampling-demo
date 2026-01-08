## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
import logging
import requests
from django.http import HttpResponse

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
