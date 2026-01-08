## Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
## SPDX-License-Identifier: Apache-2.0
from django.apps import AppConfig


class AServiceAppConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "a_service_app"
