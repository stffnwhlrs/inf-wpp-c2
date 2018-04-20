import redis
from flask import Flask, render_template, request
from datetime import datetime
from pymongo import MongoClient

app = Flask(__name__)
r = redis.Redis(host='redis', port=6379, db=0)

client = MongoClient('mongo', 27017)
db = client['inf-wpp-c2']
collection = db['aufgabe7']

# --- ROUTES ---

@app.route('/')
def start():
    return render_template('start.html')

@app.route('/redis_postcode')
def redis_postcode():
    args = request.args
    postcode = args.get('postcode')
    city = None
    state = None
    duration = None

    if postcode != None:
        starttime = datetime.now()
        result = r.hmget(postcode, 'city', 'state')
        city = result[0].decode('utf-8')
        state = result[1].decode('utf-8')
        duration = datetime.now() - starttime

    return render_template('redis_postcode.html', postcode=postcode, city=city, state=state, duration=duration)


@app.route('/redis_city')
def redis_city():
    args = request.args
    city = args.get('city')
    postcodes = []
    duration = None

    if city != None:
        starttime = datetime.now()
        city = city.upper()
        for postcode in r.scan_iter():
            if r.hmget(postcode, 'city')[0].decode('utf-8') == city:
                # decode because hashmaps are stored as binary
                postcodes.append(postcode.decode('utf-8'))
        duration = datetime.now() - starttime

    return render_template('redis_city.html', city=city, postcodes=postcodes, duration=duration)

@app.route('/mongo_postcode')
def mongo_postcode():
    args = request.args
    postcode = args.get('postcode')
    city = None
    state = None
    duration = None

    if postcode != None:
        starttime = datetime.now()
        result = collection.find_one({'_id': postcode})
        city = result['city']
        state = result['state']
        duration = datetime.now() - starttime

    return render_template('mongo_postcode.html', postcode=postcode, city=city, state=state, duration=duration)

@app.route('/mongo_city')
def mongo_city():
    args = request.args
    city = args.get('city')
    postcodes = None
    duration = None

    if city != None:
        starttime = datetime.now()
        city = city.upper()
        postcodes = collection.find({'city': city})
        duration = datetime.now() - starttime

    return render_template('mongo_city.html', city=city, postcodes=postcodes, duration=duration)


# --- ROUTES END ---

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
