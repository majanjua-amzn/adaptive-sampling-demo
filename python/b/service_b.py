from flask import Flask
import requests
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.route('/status/<int:code>')
def status(code):
    logger.info(f'Service B returning status code: {code}')
    return '', code

@app.route('/status/c/<int:code>')
def status_c(code):
    try:
        requests.get(f'http://localhost:8082/status/c/{code}')
    except:
        pass
    logger.info(f'Service B requested status code {code} from Service C')
    return '', 200

if __name__ == '__main__':
    app.run(port=8081, debug=True)