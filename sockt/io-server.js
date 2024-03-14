var io = require('socket.io')();
 var fs = require("fs");

 var fs = require('fs');
 var tempBuffer = new Buffer(0);

 function readLines(input) {
     input.on('data', function (data) {
         console.log("readLine: " + data);
         tempBuffer = Buffer.concat([tempBuffer, data]);
     });

     input.on('end', function () {
         // console.log("end" + tempBuffer);
     });
 }

 var input = fs.createReadStream('/Users/dywang/Code/MediaProjectionDemo/sockt/public/screen.png');
 readLines(input);

var clients = []
io.on('connection', function (client) {
    clients.push(client);
    console.log('client connection is ok');

    client.emit('join', 'welcome to join!!')
    // setInterval(function () {
    //     console.log("send setInterval");
    //     // client.emit('event', tempBuffer);
    // }, 600);

    client.on('chat message', function (msg) {
        console.log("receive msg=" + msg);
    });

    client.on('event', function (msg) {
        console.log("event", msg);
        console.log("event", "send image~~");
        clients.forEach(function (it) {
            it.emit('event', msg)
        })
    });
});
io.on('disconnect', function (client) {

})
io.listen(8088);