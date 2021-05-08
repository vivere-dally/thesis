from flask import Flask, request

from service.ocr import OCRService
from service.preprocessing import PreprocessingService

app = Flask(__name__)
pp_srv = PreprocessingService()
ocr_srv = OCRService(pp_srv)


@app.route('/api/scan', methods=['POST'])
def hello_world():
    if 'img' not in request.json or request.json.get('img', None) is None:
        return 'Must send a base64 encoded image', 400

    result = ocr_srv.process(request.json.get('img'))
    return result


if __name__ == '__main__':
    app.run(debug=True)
