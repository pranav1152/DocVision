from flask import send_file, Flask, request, jsonify, url_for
import flask
import werkzeug
import cv2
from Helper import adaptivethresholding, ocr

app = Flask(__name__)

@app.route('/doOP/', methods=['POST'])
def doop():
    operation = request.args.get("operation")
    files_ids = list(flask.request.files)
    file_id = files_ids[0]   
    imagefile = flask.request.files[file_id]
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    imagefile.save(filename)
    
    if operation == "adaptivethresholding":
        adaptivethresholding(filename, False)
        return jsonify({"url":"",
                        "isurl":True})
    elif operation=="ocr":
        return jsonify({"url":ocr(filename),
                        "isurl":False})
        
@app.route('/getOP/')
def getop():
    filename = request.args.get("filename")
    return send_file(filename+"_op.jpg", as_attachment = True) 
    
    
if __name__ == '__main__':
    app.run(host = "192.168.43.165", port=5000,threaded = False) 