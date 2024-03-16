var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    users = [];
const fs = require('fs');
const path = require("path");

const io = require('socket.io')(server)

//specify the html we will use
app.use('/', express.static(__dirname + '/www'));

//bind the server to the 80 port
//server.listen(3000);//for local test
server.listen(process.env.PORT || 3001);//publish to heroku

io.sockets.on('connection', function(socket) {
    console.log('a user connected');

    //new user login
    socket.on('login', function(nickname) {
        if (users.indexOf(nickname) > -1) {
            socket.emit('nickExisted');
        } else {
            //socket.userIndex = users.length;
            socket.nickname = nickname;
            users.push(nickname);
            socket.emit('loginSuccess');
            io.sockets.emit('system', nickname, users.length, 'login');
        }
    });
    //user leaves
    socket.on('disconnect', function() {
        console.log('a user disconnected');
        if (socket.nickname != null) {
            //users.splice(socket.userIndex, 1);
            users.splice(users.indexOf(socket.nickname), 1);
            socket.broadcast.emit('system', socket.nickname, users.length, 'logout');
        }
    });
    //new message get
    socket.on('postMsg', function(msg, color) {
        socket.broadcast.emit('newMsg', socket.nickname, msg, color);
    });
    //new image get
    socket.on('image', function(imgData, color) {
        console.log('image received');
        //socket.broadcast.emit('newImg', socket.nickname, imgData, color);
        var savePath = path.join(__dirname, "www",'screen.jpeg');
        fs.writeFile(savePath, imgData, function (err) {
            if (err) {
                console.log(err);
            } else {
                var pathWithTime = "screen.jpeg?t=" + new Date().getTime();
                console.log('image received, updateImage: ' + pathWithTime);
                socket.broadcast.emit('updateImage', socket.nickname, pathWithTime, color);
            }
        });
    });
});
