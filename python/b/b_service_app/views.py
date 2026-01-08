## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
import logging
import requests
from django.http import HttpResponse

logger = logging.getLogger(__name__)

def status(request, code):
    logger.info(f'Service B returning status code: {code}')
    return HttpResponse(f'Service B returning status code: {code}', status=code)

def status_c(request, code):
    try:
        requests.get(f'http://localhost:8082/status/c/{code}')
    except Exception:
        pass
    logger.info(f'Service B requested status code {code} from Service C')
    return HttpResponse('', status=200)
