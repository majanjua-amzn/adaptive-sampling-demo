## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
from django.urls import path
from b_service_app import views

urlpatterns = [
    path('status/<int:code>', views.status),
    path('status/c/<int:code>', views.status_c),
]
