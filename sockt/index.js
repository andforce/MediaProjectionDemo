const express = require('express');
const app = express();
const http = require('http').Server(app);
const fs = require('fs');
const path = require('path');
const io = require('socket.io')(65535, {
    cors: {
        origin: "http://localhost:63343",
        methods: ["GET", "POST"]
    }
});

io.on('connection', function (socket) {
    console.log('a user connected');
    socket.on('image', function (msg) {
        console.log('image received');
        fs.writeFile(path.join(__dirname, "public",'screen.png'), msg, function (err) {
            if (err) {
                console.log(err);
            } else {
                io.emit('imageUpdated');
            }
        });
    });
});

app.use(express.static(path.join(__dirname, 'public'), {
    setHeaders: function (res, path) {
        if (path.endsWith('screen.png')) {
            res.setHeader('Cache-Control', 'no-store');
        }
    }
}));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

http.listen(3000, function () {
    console.log('listening on *:3000');
});