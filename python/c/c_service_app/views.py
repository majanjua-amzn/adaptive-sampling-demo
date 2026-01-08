## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
import logging
from django.http import HttpResponse

logger = logging.getLogger(__name__)

def status_c(request, code):
    logger.info(f'Service C returning status code: {code}')
    return HttpResponse('', status=code)
