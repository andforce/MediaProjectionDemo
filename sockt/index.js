var express = require('express')
var app = express();
const path = require('path');
var http = require('http').Server(app);
var fs = require("fs");

var io = require('socket.io')(65535);

io.on('connection', function (socket) {
    console.log('a user connected');

    socket.on('image', function (msg) {
        console.log('image received');
        // broadcast the image to all connected clients
        //io.emit('imageUpdated', msg);
        // 保存到public目录下
        var time = new Date().getTime();
        var path = './public/screen.png';
        io.emit('imageUpdated', time);

        fs.writeFile(path, msg, function (err) {
            if (err) {
                console.log(err);
            }
             io.emit('imageUpdated', time);
        });
    });
    // 监听客户端断开
    socket.on('disconnect', function () {
        console.log('a user disconnected');
    });
});


// 设置静态文件目录
app.use(express.static(path.join(__dirname, 'public')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

http.listen(3000, function () {
  console.log('listening on *:3000');
});
