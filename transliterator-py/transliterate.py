# aksharamukha_server
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
	app.run()
