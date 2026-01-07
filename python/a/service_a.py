from flask import Flask
import requests
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.route('/')
def hello():
    logger.info('/ called')
    return 'Success - /'

@app.route('/healthcheck')
def healthcheck():
    logger.info('/healthcheck called')
    return 'Success - /healthcheck'

@app.route('/status/a/<int:code>')
def status_a(code):
    logger.info(f'Service A returning status code: {code}')
    return '', code

@app.route('/status/b/<int:code>')
def status_b(code):
    try:
        requests.get(f'http://localhost:8081/status/{code}')
    except:
        pass
    logger.info(f'Service A requested status code {code} from Service B')
    return '', 200

@app.route('/status/c/<int:code>')
def status_c(code):
    try:
        requests.get(f'http://localhost:8081/status/c/{code}')
    except:
        pass
    logger.info(f'Service A requested status code {code} from Service C through Service B')
    return '', 200

if __name__ == '__main__':
    app.run(port=8080, debug=True)