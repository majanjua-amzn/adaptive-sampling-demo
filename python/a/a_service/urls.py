## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
from django.urls import path
from a_service_app import views

urlpatterns = [
    path('', views.healthcheck),
    path('outgoing-http-call', views.http_call),
    path('status/a/<int:code>', views.status_a),
    path('status/b/<int:code>', views.status_b),
    path('status/c/<int:code>', views.status_c),
]
