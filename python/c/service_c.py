from flask import Flask
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.route('/status/c/<int:code>')
def status_c(code):
    logger.info(f'Service C returning status code: {code}')
    return '', code

if __name__ == '__main__':
    app.run(port=8082, debug=True)