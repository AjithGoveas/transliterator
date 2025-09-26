# aksharamukha_server
import os
from flask import Flask, request
from aksharamukha import transliterate

app = Flask(__name__)

@app.route('/')
def index():
	return {'message': 'Aksharamukha Transliteration API'}

@app.route('/transliterate', methods=['POST'])
def translit():
	data = request.json
	if data is None:
		return {'error': 'No JSON data provided'}, 400
	result = transliterate.process(data['source'], data['target'], data['text'])
	return {'result': result}

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))  # Render sets PORT env variable
    app.run(host='0.0.0.0', port=port)
