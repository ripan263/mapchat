# pip install flask flask-restful flask-jsonpify # (--user)

# Sample requesting locations list:
# requests.get("http://127.0.0.1:5002/chat").json()
# Sample putting location.
# r = requests.put("http://127.0.0.1:5002/chat", json={"user_id" : "John Swaggles", "message" : "Hi everyone", "location": "1235234, 324234"})

from flask import Flask, request
from flask_restful import Resource, Api
# from json import dumps
from flask_jsonpify import jsonify
from datetime import datetime
import sys
import itertools


app = Flask(__name__)
api = Api(app)

# Dictionary where all msgs on server are stored.
global msgs
msgs = []


def addMsg(d):
    global msgs
    # add current time..
    now = datetime.now()
    d['time'] = now
    msgs.append(d)

    filterMsgs()


# filter times that are too old.
def filterMsgs():
    global msgs
    now = datetime.now()
    msgs = list(itertools.
                dropwhile(lambda m: (now - m['time'])
                          .total_seconds() > 10, msgs))


def printMsgs():
    print("------------------------------", file=sys.stderr)
    print("List of msgs currently stored:", file=sys.stderr)
    global msgs
    for m in msgs:
        printMsg(m)


def printMsg(m):
    now = datetime.now()
    t_diff = (now - m['time']).total_seconds()
    user_id = m['user_id']
    message = m['message']
    location = m['location']

    print("-------\n",
          "User_id:", user_id,
          "\nSent msg:", message,
          "\nAt location:", location,
          "\n", t_diff, "seconds ago",
          file=sys.stderr)


class Chat(Resource):
    # Get list of all chat msgs (in past w/e mins)
    def get(self):
        global msgs
        filterMsgs()
        return jsonify(msgs)

    # Add a new message.
    # JSON must contain 'user_id', 'message', 'location'.
    def put(self):
        global msgs
        d = request.get_json()

        if 'user_id' in d and 'message' in d and 'location' in d:
            addMsg(d)
            printMsgs()
            return jsonify({"resp": "Message posting sucessful"})
        else:
            print("ERROR: Invalid json sent as put request.\n", d, file=sys.stderr)
            return jsonify({"resp": "Error, invalid request."})


api.add_resource(Chat, '/chat')  # Route_1


if __name__ == '__main__':
    app.run(port=5002)
