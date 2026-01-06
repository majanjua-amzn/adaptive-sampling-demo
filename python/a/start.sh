#!/bin/bash
python3 -m pip install -r requirements.txt
python3 -m pip install /Users/majanjua/Documents/aws-otel-python-instrumentation/dist/aws_opentelemetry_distro-0.12.1.dev0-py3-none-any.whl
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
export OTEL_AWS_APPLICATION_SIGNALS_ENABLED=true
export OTEL_PYTHON_DISTRO=aws_distro
export OTEL_PYTHON_CONFIGURATOR=aws_configurator
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_TRACES_SAMPLER=xray
export OTEL_TRACES_SAMPLER_ARG="endpoint=http://localhost:2000"
export OTEL_AWS_APPLICATION_SIGNALS_EXPORTER_ENDPOINT=http://localhost:4316/v1/metrics
export OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://localhost:4316/v1/traces
export OTEL_RESOURCE_ATTRIBUTES="service.name=ServiceA"
opentelemetry-instrument python3 service_a.py